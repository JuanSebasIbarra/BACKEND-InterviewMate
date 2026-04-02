export type ModoAutenticacion = "ingreso" | "registro";
export type VistaActiva = "inicio" | "entrevista";
export type NivelEntrevista = "1" | "2" | "3";
export type ProfesionEntrevista =
  | "Ingenieria"
  | "Medicina"
  | "Psicologia"
  | "Diseno"
  | "Marketing"
  | "Finanzas"
  | "Derecho"
  | "Educacion";

export interface ValoresIngreso {
  usuario: string;
  contrasena: string;
}

export interface ValoresRegistro {
  usuario: string;
  correo: string;
  contrasena: string;
  confirmarContrasena: string;
}

export interface ValoresConfiguracion {
  nivel: NivelEntrevista;
  profesion: ProfesionEntrevista;
  cargo: string;
  empresa: string;
}

export interface PerfilUsuario {
  usuario: string;
  correo: string;
}

export interface PreguntaEntrevista {
  id: string;
  orden: number;
  enunciado: string;
  audioUrl?: string;
  transcripcion?: string;
  duracionSegundos?: number;
  puntaje?: number;
  retroalimentacion?: string;
  recomendacion?: string;
}

export interface SesionEntrevista {
  id: string;
  fechaInicio: string;
  fechaFin?: string;
  nivel: NivelEntrevista;
  profesion: ProfesionEntrevista;
  cargo: string;
  empresa: string;
  puntajeTotal?: number;
  preguntas: PreguntaEntrevista[];
}

export interface HistorialEntrevista {
  id: string;
  titulo: string;
  subtitulo: string;
  fecha: string;
  puntajeTotal: number;
  nivel: NivelEntrevista;
  recomendacionesPorPregunta: {
    pregunta: string;
    recomendacion: string;
    puntaje: number;
  }[];
}

export interface SesionAutenticada {
  token: string;
  usuario: string;
  correo: string;
}

export interface EstadoGrabadora {
  esCompatible: boolean;
  estaGrabando: boolean;
  duracionActual: number;
  audioActualUrl?: string;
  transcripcionActual?: string;
  reconocimientoDisponible: boolean;
}

export interface EstadoVoz {
  habilitada: boolean;
  estaHablando: boolean;
}
