export function formatearFecha(fecha: string) {
  return new Intl.DateTimeFormat("es-CO", { dateStyle: "medium", timeStyle: "short" }).format(new Date(fecha));
}

export function formatearPorcentaje(valor?: number) {
  if (typeof valor !== "number") {
    return "Pendiente";
  }

  return `${Math.round(valor)}%`;
}

export function obtenerIniciales(nombre?: string) {
  if (!nombre) {
    return "IA";
  }

  return nombre
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((pieza) => pieza[0]?.toUpperCase())
    .join("");
}

export function crearId(prefijo: string) {
  return `${prefijo}-${Math.random().toString(36).slice(2, 10)}`;
}

export function calcularPuntaje(duracionSegundos: number, indice: number) {
  const base = Math.min(95, 56 + duracionSegundos * 2 + indice);
  return Math.max(50, Math.round(base));
}
