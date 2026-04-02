import { construirPreguntas, obtenerPalabrasClave } from "@/data/catalog";
import {
  HistorialEntrevista,
  NivelEntrevista,
  PerfilUsuario,
  PreguntaEntrevista,
  ProfesionEntrevista,
  SesionAutenticada,
  SesionEntrevista,
  ValoresConfiguracion,
  ValoresRegistro
} from "@/types";
import { calcularPuntaje, crearId } from "@/lib/utils";

const baseApi = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
const usarMocks = (process.env.NEXT_PUBLIC_USE_MOCKS ?? "true") === "true";
const claveUsuariosMock = "interviewmate.usuarios.mock";
const claveHistorialMock = "interviewmate.historial.mock";
const clavePreguntasUsadas = "interviewmate.preguntas.usadas";

const usuarioDemo = {
  usuario: "camilo.demo",
  correo: "camilo.demo@interviewmate.com",
  contrasena: "Demo12345*"
};

async function solicitar<T>(ruta: string, init?: RequestInit, token?: string): Promise<T> {
  const respuesta = await fetch(`${baseApi}${ruta}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init?.headers ?? {})
    }
  });

  if (!respuesta.ok) {
    const texto = await respuesta.text();
    throw new Error(texto || "La solicitud no pudo completarse.");
  }

  if (respuesta.status === 204) {
    return {} as T;
  }

  return respuesta.json() as Promise<T>;
}

function obtenerUsuariosMock() {
  if (typeof window === "undefined") {
    return [usuarioDemo];
  }

  const bruto = window.localStorage.getItem(claveUsuariosMock);
  if (!bruto) {
    const base = [usuarioDemo];
    window.localStorage.setItem(claveUsuariosMock, JSON.stringify(base));
    return base;
  }

  return JSON.parse(bruto) as typeof usuarioDemo[];
}

function guardarUsuariosMock(usuarios: typeof usuarioDemo[]) {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(claveUsuariosMock, JSON.stringify(usuarios));
}

function obtenerHistorialMock() {
  if (typeof window === "undefined") {
    return {} as Record<string, HistorialEntrevista[]>;
  }

  const bruto = window.localStorage.getItem(claveHistorialMock);
  return bruto ? (JSON.parse(bruto) as Record<string, HistorialEntrevista[]>) : {};
}

function guardarHistorialMock(historial: Record<string, HistorialEntrevista[]>) {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(claveHistorialMock, JSON.stringify(historial));
}

function obtenerPreguntasUsadas(usuario: string) {
  if (typeof window === "undefined") {
    return [] as string[];
  }

  const bruto = window.localStorage.getItem(clavePreguntasUsadas);
  const mapa = bruto ? (JSON.parse(bruto) as Record<string, string[]>) : {};
  return mapa[usuario] ?? [];
}

function guardarPreguntasUsadas(usuario: string, preguntas: string[]) {
  if (typeof window === "undefined") {
    return;
  }

  const bruto = window.localStorage.getItem(clavePreguntasUsadas);
  const mapa = bruto ? (JSON.parse(bruto) as Record<string, string[]>) : {};
  mapa[usuario] = preguntas.slice(-80);
  window.localStorage.setItem(clavePreguntasUsadas, JSON.stringify(mapa));
}

export function obtenerCredencialesDemo() {
  return usuarioDemo;
}

export async function registrarUsuario(valores: ValoresRegistro) {
  if (usarMocks) {
    const usuarios = obtenerUsuariosMock();
    const existe = usuarios.some((item) => item.usuario === valores.usuario || item.correo === valores.correo);
    if (existe) {
      throw new Error("El usuario o el correo ya existen.");
    }

    usuarios.push({
      usuario: valores.usuario,
      correo: valores.correo,
      contrasena: valores.contrasena
    });
    guardarUsuariosMock(usuarios);
    return;
  }

  await solicitar("/auth/register", { method: "POST", body: JSON.stringify(valores) });
}

export async function ingresarUsuario({
  usuario,
  contrasena
}: {
  usuario: string;
  contrasena: string;
}): Promise<SesionAutenticada> {
  if (usarMocks) {
    const usuarios = obtenerUsuariosMock();
    const encontrado = usuarios.find((item) => item.usuario === usuario && item.contrasena === contrasena);

    if (!encontrado) {
      throw new Error("Usuario o contraseña inválidos.");
    }

    return {
      token: `mock-token-${usuario}`,
      usuario: encontrado.usuario,
      correo: encontrado.correo
    };
  }

  const inicio = await solicitar<{ token: string }>("/auth/login", {
    method: "POST",
    body: JSON.stringify({
      username: usuario,
      password: contrasena,
      email: `${usuario}@placeholder.app`,
      confirmPassword: contrasena
    })
  });

  const perfil = await solicitar<{ username: string; email: string }>("/auth/me", { method: "GET" }, inicio.token);
  return {
    token: inicio.token,
    usuario: perfil.username,
    correo: perfil.email
  };
}

export async function obtenerPerfil(token?: string): Promise<PerfilUsuario | undefined> {
  if (usarMocks) {
    return undefined;
  }

  if (!token) {
    return undefined;
  }

  const perfil = await solicitar<{ username: string; email: string }>("/auth/me", { method: "GET" }, token);
  return { usuario: perfil.username, correo: perfil.email };
}

export async function obtenerHistorial(usuario?: string, token?: string): Promise<HistorialEntrevista[]> {
  if (usarMocks || !token) {
    const historial = obtenerHistorialMock();
    return usuario ? (historial[usuario] ?? []) : [];
  }

  const respuesta = await solicitar<{
    data: { id: string; generatedAt: string; totalScore: number; status: string }[];
  }>("/api/v1/results/me", { method: "GET" }, token);

  return respuesta.data.map((item, indice) => ({
    id: item.id,
    titulo: `Entrevista ${indice + 1}`,
    subtitulo: item.status,
    fecha: item.generatedAt,
    puntajeTotal: item.totalScore,
    nivel: "2",
    recomendacionesPorPregunta: []
  }));
}

export async function iniciarEntrevista(configuracion: ValoresConfiguracion, usuario: string): Promise<SesionEntrevista> {
  const usadas = obtenerPreguntasUsadas(usuario);
  const preguntas = construirPreguntas({
    profesion: configuracion.profesion,
    cargo: configuracion.cargo,
    nivel: configuracion.nivel,
    empresa: configuracion.empresa,
    usadas
  });

  guardarPreguntasUsadas(
    usuario,
    usadas.concat(preguntas.map((pregunta) => pregunta.enunciado))
  );

  return {
    id: crearId("sesion"),
    fechaInicio: new Date().toISOString(),
    nivel: configuracion.nivel,
    profesion: configuracion.profesion,
    cargo: configuracion.cargo,
    empresa: configuracion.empresa,
    preguntas
  };
}

export async function evaluarRespuesta({
  sesion,
  preguntaActual,
  duracionSegundos,
  audioUrl,
  transcripcion
}: {
  sesion: SesionEntrevista;
  preguntaActual: PreguntaEntrevista;
  duracionSegundos: number;
  audioUrl: string;
  transcripcion: string;
}) {
  const palabrasClave = obtenerPalabrasClave(sesion.profesion, sesion.cargo);
  const textoNormalizado = transcripcion.toLowerCase();
  const coincidencias = palabrasClave.filter((palabra) => textoNormalizado.includes(palabra.toLowerCase()));
  const ratioCoincidencia = palabrasClave.length ? coincidencias.length / Math.min(6, palabrasClave.length) : 0;
  const puntajeBase = calcularPuntaje(duracionSegundos, preguntaActual.orden);
  const puntajeContenido = Math.round(Math.min(100, ratioCoincidencia * 100));
  const penalizacionSinTexto = transcripcion.trim().length < 12 ? 26 : 0;
  const puntaje = Math.max(35, Math.min(100, Math.round(puntajeBase * 0.45 + puntajeContenido * 0.55 - penalizacionSinTexto)));
  const recomendacionPorNivel: Record<NivelEntrevista, string> = {
    "1": "Amplía un poco más la idea principal y usa un ejemplo concreto.",
    "2": "Da más contexto, explica tu criterio y agrega resultados medibles.",
    "3": "Refuerza tu liderazgo, el impacto estratégico y la toma de decisiones."
  };

  let retroalimentacion = "La respuesta tiene elementos útiles, pero todavía puede fortalecerse con más precisión.";

  if (transcripcion.trim().length < 12) {
    retroalimentacion = "No se detectó suficiente contenido en la respuesta. Intenta hablar con más claridad y detalle.";
  } else if (puntaje >= 85) {
    retroalimentacion = "La respuesta muestra buena relación con la pregunta, claridad en la idea y una base profesional convincente.";
  } else if (puntaje >= 70) {
    retroalimentacion = "La respuesta es aceptable y tiene relación con el cargo, aunque todavía puede ser más específica y sólida.";
  } else if (puntaje >= 55) {
    retroalimentacion = "La respuesta necesita más precisión frente al tema preguntado y más coincidencia con el perfil del cargo.";
  }

  return {
    ...preguntaActual,
    audioUrl,
    transcripcion,
    duracionSegundos,
    puntaje,
    retroalimentacion,
    recomendacion: `${recomendacionPorNivel[sesion.nivel]} Palabras clave detectadas: ${coincidencias.slice(0, 4).join(", ") || "ninguna relevante"}.`
  };
}

export async function cerrarEntrevista({
  sesion,
  usuario
}: {
  sesion: SesionEntrevista;
  usuario: string;
}) {
  const puntajeTotal = Math.round(
    sesion.preguntas.reduce((acumulado, pregunta) => acumulado + (pregunta.puntaje ?? 0), 0) / sesion.preguntas.length
  );

  const recomendacionesPorPregunta = sesion.preguntas.map((pregunta) => ({
    pregunta: pregunta.enunciado,
    recomendacion: pregunta.recomendacion ?? "Sin recomendación",
    puntaje: pregunta.puntaje ?? 0
  }));

  const historialActual = obtenerHistorialMock();
  const nuevoRegistro: HistorialEntrevista = {
    id: sesion.id,
    titulo: `${sesion.cargo} - ${sesion.empresa}`,
    subtitulo: sesion.profesion,
    fecha: new Date().toISOString(),
    puntajeTotal,
    nivel: sesion.nivel,
    recomendacionesPorPregunta
  };

  guardarHistorialMock({
    ...historialActual,
    [usuario]: [nuevoRegistro, ...(historialActual[usuario] ?? [])]
  });

  return {
    ...sesion,
    fechaFin: new Date().toISOString(),
    puntajeTotal
  };
}

export function describirProfesion(profesion: ProfesionEntrevista) {
  const etiquetas: Record<ProfesionEntrevista, string> = {
    Ingenieria: "tecnico y orientado a soluciones",
    Medicina: "clinico, humano y cuidadoso",
    Psicologia: "empatico, analitico y etico",
    Diseno: "creativo y centrado en experiencia",
    Marketing: "estrategico y enfocado en resultados",
    Finanzas: "preciso, analitico y orientado al riesgo",
    Derecho: "argumentativo, riguroso y normativo",
    Educacion: "pedagogico, humano y formativo"
  };

  return etiquetas[profesion];
}
