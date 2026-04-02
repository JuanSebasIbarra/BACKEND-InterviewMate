import { SesionAutenticada } from "@/types";

const claveSesion = "interviewmate.sesion.segura";

function obtenerSemilla() {
  return process.env.NEXT_PUBLIC_SESSION_SEED ?? "interviewmate-premium-seed";
}

function bufferABase64(buffer: ArrayBuffer) {
  const bytes = new Uint8Array(buffer);
  let resultado = "";
  bytes.forEach((byte) => {
    resultado += String.fromCharCode(byte);
  });
  return btoa(resultado);
}

function base64ABuffer(valor: string) {
  const binario = atob(valor);
  const bytes = new Uint8Array(binario.length);
  for (let indice = 0; indice < binario.length; indice += 1) {
    bytes[indice] = binario.charCodeAt(indice);
  }
  return bytes.buffer;
}

async function derivarLlave() {
  const codificador = new TextEncoder();
  const material = await window.crypto.subtle.importKey("raw", codificador.encode(obtenerSemilla()), "PBKDF2", false, ["deriveKey"]);

  return window.crypto.subtle.deriveKey(
    { name: "PBKDF2", salt: codificador.encode("interviewmate-salt"), iterations: 120000, hash: "SHA-256" },
    material,
    { name: "AES-GCM", length: 256 },
    false,
    ["encrypt", "decrypt"]
  );
}

export async function guardarSesionSegura(sesion: SesionAutenticada) {
  if (typeof window === "undefined" || !window.crypto?.subtle) {
    return;
  }

  const codificador = new TextEncoder();
  const iv = window.crypto.getRandomValues(new Uint8Array(12));
  const llave = await derivarLlave();
  const cifrado = await window.crypto.subtle.encrypt({ name: "AES-GCM", iv }, llave, codificador.encode(JSON.stringify(sesion)));

  window.sessionStorage.setItem(
    claveSesion,
    JSON.stringify({
      iv: bufferABase64(iv.buffer),
      carga: bufferABase64(cifrado)
    })
  );
}

export async function cargarSesionSegura() {
  if (typeof window === "undefined" || !window.crypto?.subtle) {
    return null;
  }

  const bruto = window.sessionStorage.getItem(claveSesion);
  if (!bruto) {
    return null;
  }

  try {
    const datos = JSON.parse(bruto) as { iv: string; carga: string };
    const llave = await derivarLlave();
    const descifrado = await window.crypto.subtle.decrypt(
      { name: "AES-GCM", iv: new Uint8Array(base64ABuffer(datos.iv)) },
      llave,
      base64ABuffer(datos.carga)
    );

    return JSON.parse(new TextDecoder().decode(descifrado)) as SesionAutenticada;
  } catch {
    window.sessionStorage.removeItem(claveSesion);
    return null;
  }
}

export function limpiarSesionSegura() {
  if (typeof window === "undefined") {
    return;
  }

  window.sessionStorage.removeItem(claveSesion);
}
