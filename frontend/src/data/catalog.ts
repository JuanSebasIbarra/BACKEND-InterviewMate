import { NivelEntrevista, ProfesionEntrevista } from "@/types";

export const opcionesNivel: NivelEntrevista[] = ["1", "2", "3"];

export const opcionesProfesion: { profesion: ProfesionEntrevista; cargos: string[] }[] = [
  { profesion: "Ingenieria", cargos: ["Frontend Developer", "Backend Developer", "Data Engineer", "DevOps Engineer", "QA Analyst"] },
  { profesion: "Medicina", cargos: ["Medico General", "Pediatra", "Enfermero Jefe", "Medico de Urgencias", "Coordinador Clinico"] },
  { profesion: "Psicologia", cargos: ["Psicologo Clinico", "Psicologo Organizacional", "Analista de Seleccion", "Terapeuta", "Especialista en Bienestar"] },
  { profesion: "Diseno", cargos: ["UX Designer", "Product Designer", "Diseñador Visual", "Investigador UX", "Director Creativo"] },
  { profesion: "Marketing", cargos: ["Growth Specialist", "Content Strategist", "Brand Manager", "CRM Manager", "Paid Media Specialist"] },
  { profesion: "Finanzas", cargos: ["Analista Financiero", "Contador", "Analista de Riesgo", "Controller", "Asociado de Inversion"] },
  { profesion: "Derecho", cargos: ["Abogado Junior", "Abogado Corporativo", "Analista Legal", "Asesor de Cumplimiento", "Especialista Contractual"] },
  { profesion: "Educacion", cargos: ["Docente", "Coordinador Academico", "Orientador Escolar", "Diseñador Instruccional", "Director de Programa"] }
];

const temasPorProfesion: Record<ProfesionEntrevista, string[]> = {
  Ingenieria: ["arquitectura", "depuracion", "rendimiento", "escalabilidad", "calidad", "pruebas", "integracion", "mantenimiento", "analisis tecnico", "seguridad"],
  Medicina: ["diagnostico", "triage", "seguridad del paciente", "protocolos", "comunicacion clinica", "urgencias", "seguimiento", "priorizacion", "historia medica", "etica"],
  Psicologia: ["escucha activa", "evaluacion", "intervencion", "analisis conductual", "empatia", "confidencialidad", "acompañamiento", "contexto emocional", "observacion", "seguimiento terapeutico"],
  Diseno: ["investigacion", "prototipado", "usabilidad", "consistencia visual", "sistema de diseño", "hallazgos", "iteracion", "accesibilidad", "criterio visual", "validacion"],
  Marketing: ["audiencia", "conversion", "campañas", "segmentacion", "contenido", "analitica", "retorno", "marca", "experimentos", "crecimiento"],
  Finanzas: ["analisis financiero", "riesgo", "presupuesto", "control", "rentabilidad", "indicadores", "proyecciones", "cumplimiento", "tesoreria", "decisiones"],
  Derecho: ["normativa", "contratos", "riesgo legal", "argumentacion", "cumplimiento", "interpretacion", "negociacion", "documentacion", "respaldo juridico", "prevencion"],
  Educacion: ["planeacion", "evaluacion", "acompañamiento", "didactica", "aprendizaje", "seguimiento", "inclusion", "metodologias", "comunicacion", "mejora continua"]
};

const enfoquesPorNivel: Record<NivelEntrevista, string[]> = {
  "1": ["base", "operativo", "aprendizaje", "adaptación"],
  "2": ["resolución", "colaboración", "criterio", "resultados"],
  "3": ["liderazgo", "estrategia", "impacto", "decisión"]
};

const plantillas = [
  "Cuéntame una situación real donde aplicaste {tema} para desempeñarte como {cargo}.",
  "¿Cómo abordarías un escenario en {empresa} donde {tema} fuera determinante para el cargo de {cargo}?",
  "Describe una experiencia en la que tuviste que demostrar {enfoque} y {tema} dentro de {profesion}.",
  "¿Qué harías durante tus primeros noventa días en {empresa} para destacar en {cargo} usando {tema}?",
  "Explícame cómo medirías un buen resultado en {cargo} cuando interviene {tema}.",
  "Háblame de un error profesional relacionado con {tema} y cómo lo corregiste.",
  "¿Cómo priorizarías decisiones si en tu trabajo como {cargo} surge una situación crítica asociada a {tema}?",
  "¿Qué ejemplo darías para demostrar que tienes criterio en {tema} aplicado a {profesion}?",
  "Si tuvieras que enseñar a otro profesional cómo trabajar {tema} en {cargo}, ¿qué le dirías?",
  "¿Cómo demostrarías que tu experiencia en {tema} puede aportar valor real a {empresa}?"
];

export function obtenerCargosPorProfesion(profesion: ProfesionEntrevista) {
  return opcionesProfesion.find((item) => item.profesion === profesion)?.cargos ?? [];
}

export function construirPreguntas({
  profesion,
  cargo,
  nivel,
  empresa,
  usadas
}: {
  profesion: ProfesionEntrevista;
  cargo: string;
  nivel: NivelEntrevista;
  empresa: string;
  usadas: string[];
}) {
  const temas = [...temasPorProfesion[profesion]];
  const enfoques = enfoquesPorNivel[nivel];
  const preguntas = new Set<string>();

  for (let indice = 0; indice < temas.length && preguntas.size < 10; indice += 1) {
    const tema = temas[indice];
    const enfoque = enfoques[indice % enfoques.length];
    const plantilla = plantillas[indice % plantillas.length];
    const pregunta = plantilla
      .replaceAll("{tema}", tema)
      .replaceAll("{cargo}", cargo)
      .replaceAll("{empresa}", empresa)
      .replaceAll("{profesion}", profesion.toLowerCase())
      .replaceAll("{enfoque}", enfoque);

    if (!usadas.includes(pregunta)) {
      preguntas.add(pregunta);
    }
  }

  if (preguntas.size < 10) {
    const preguntasExtra = plantillas.map((plantilla, indice) =>
      plantilla
        .replaceAll("{tema}", temas[indice % temas.length])
        .replaceAll("{cargo}", cargo)
        .replaceAll("{empresa}", empresa)
        .replaceAll("{profesion}", profesion.toLowerCase())
        .replaceAll("{enfoque}", enfoques[(indice + 1) % enfoques.length])
    );

    preguntasExtra.forEach((pregunta) => {
      if (preguntas.size < 10 && !usadas.includes(pregunta)) {
        preguntas.add(pregunta);
      }
    });
  }

  return Array.from(preguntas)
    .slice(0, 10)
    .map((enunciado, indice) => ({
      id: `pregunta-${indice + 1}-${Math.random().toString(36).slice(2, 7)}`,
      orden: indice + 1,
      enunciado
    }));
}

export function obtenerPalabrasClave(profesion: ProfesionEntrevista, cargo: string) {
  const base = temasPorProfesion[profesion];
  const cargoNormalizado = cargo.toLowerCase().split(/\s+/);
  return Array.from(new Set([...base, ...cargoNormalizado]));
}
