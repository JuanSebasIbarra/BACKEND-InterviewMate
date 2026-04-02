"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { obtenerCargosPorProfesion } from "@/data/catalog";
import {
  cerrarEntrevista,
  describirProfesion,
  evaluarRespuesta,
  ingresarUsuario,
  iniciarEntrevista,
  obtenerCredencialesDemo,
  obtenerHistorial,
  registrarUsuario
} from "@/lib/api";
import { cargarSesionSegura, guardarSesionSegura, limpiarSesionSegura } from "@/lib/secure-session";
import {
  EstadoGrabadora,
  EstadoVoz,
  HistorialEntrevista,
  ModoAutenticacion,
  PerfilUsuario,
  SesionEntrevista,
  ValoresConfiguracion,
  ValoresIngreso,
  ValoresRegistro,
  VistaActiva
} from "@/types";

declare global {
  interface Window {
    SpeechRecognition?: new () => SpeechRecognition;
    webkitSpeechRecognition?: new () => SpeechRecognition;
  }
}

interface SpeechRecognition extends EventTarget {
  continuous: boolean;
  interimResults: boolean;
  lang: string;
  onresult: ((event: SpeechRecognitionEvent) => void) | null;
  onend: (() => void) | null;
  start(): void;
  stop(): void;
}

interface SpeechRecognitionEvent {
  resultIndex: number;
  results: {
    [key: number]: {
      [key: number]: {
        transcript: string;
      };
      isFinal: boolean;
      length: number;
    };
    length: number;
  };
}

const valoresIngresoIniciales: ValoresIngreso = { usuario: "", contrasena: "" };
const valoresRegistroIniciales: ValoresRegistro = { usuario: "", correo: "", contrasena: "", confirmarContrasena: "" };
const valoresConfiguracionIniciales: ValoresConfiguracion = {
  nivel: "2",
  profesion: "Ingenieria",
  cargo: "Frontend Developer",
  empresa: "InterviewMate"
};

export function useInterviewApp() {
  const [modoAutenticacion, setModoAutenticacion] = useState<ModoAutenticacion>("ingreso");
  const [vistaActiva, setVistaActiva] = useState<VistaActiva>("inicio");
  const [formularioIngreso, setFormularioIngreso] = useState(valoresIngresoIniciales);
  const [formularioRegistro, setFormularioRegistro] = useState(valoresRegistroIniciales);
  const [formularioConfiguracion, setFormularioConfiguracion] = useState(valoresConfiguracionIniciales);
  const [cargandoAutenticacion, setCargandoAutenticacion] = useState(false);
  const [errorAutenticacion, setErrorAutenticacion] = useState<string>();
  const [tokenSesion, setTokenSesion] = useState<string>();
  const [usuarioActual, setUsuarioActual] = useState<PerfilUsuario>();
  const [historial, setHistorial] = useState<HistorialEntrevista[]>([]);
  const [sesionEntrevista, setSesionEntrevista] = useState<SesionEntrevista>();
  const [indicePreguntaActual, setIndicePreguntaActual] = useState(0);
  const [estadoVoz, setEstadoVoz] = useState<EstadoVoz>({ habilitada: true, estaHablando: false });
  const [estadoGrabadora, setEstadoGrabadora] = useState<EstadoGrabadora>({
    esCompatible: typeof window !== "undefined" ? "MediaRecorder" in window : false,
    estaGrabando: false,
    duracionActual: 0,
    reconocimientoDisponible: typeof window !== "undefined" ? Boolean(window.SpeechRecognition || window.webkitSpeechRecognition) : false
  });
  const [sesionCargada, setSesionCargada] = useState(false);

  const referenciaGrabadora = useRef<MediaRecorder | null>(null);
  const referenciaFragmentos = useRef<BlobPart[]>([]);
  const referenciaTemporizador = useRef<number | null>(null);
  const referenciaPreguntaLeida = useRef<string>("");
  const referenciaAutoEnvio = useRef(false);
  const referenciaReconocimiento = useRef<SpeechRecognition | null>(null);
  const referenciaTranscripcion = useRef("");

  function hablarTexto(texto: string) {
    if (typeof window === "undefined" || !("speechSynthesis" in window)) {
      return;
    }

    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(texto);
    utterance.lang = "es-ES";
    utterance.rate = 0.92;
    utterance.pitch = 1;
    utterance.onstart = () => setEstadoVoz((anterior) => ({ ...anterior, estaHablando: true }));
    utterance.onend = () => setEstadoVoz((anterior) => ({ ...anterior, estaHablando: false }));
    utterance.onerror = () => setEstadoVoz((anterior) => ({ ...anterior, estaHablando: false }));
    window.speechSynthesis.speak(utterance);
  }

  useEffect(() => {
    const hidratar = async () => {
      const sesion = await cargarSesionSegura();
      if (sesion) {
        setTokenSesion(sesion.token);
        setUsuarioActual({ usuario: sesion.usuario, correo: sesion.correo });
        setHistorial(await obtenerHistorial(sesion.usuario, sesion.token));
      }
      setSesionCargada(true);
    };

    hidratar();
  }, []);

  useEffect(() => {
    const cargos = obtenerCargosPorProfesion(formularioConfiguracion.profesion);
    if (!cargos.includes(formularioConfiguracion.cargo)) {
      setFormularioConfiguracion((anterior) => ({
        ...anterior,
        cargo: cargos[0] ?? ""
      }));
    }
  }, [formularioConfiguracion.profesion, formularioConfiguracion.cargo]);

  useEffect(() => {
    if (!sesionEntrevista || !estadoVoz.habilitada) {
      return;
    }

    const pregunta = sesionEntrevista.preguntas[indicePreguntaActual];
    if (!pregunta || typeof window === "undefined" || !("speechSynthesis" in window)) {
      return;
    }

    if (referenciaPreguntaLeida.current === pregunta.id) {
      return;
    }

    referenciaPreguntaLeida.current = pregunta.id;

    const mensajeInicial =
      indicePreguntaActual === 0 && usuarioActual
        ? `Bienvenido ${usuarioActual.usuario}. Te deseo muchos exitos. Primera pregunta. ${pregunta.enunciado}`
        : pregunta.enunciado;
    hablarTexto(mensajeInicial);

    return () => {
      window.speechSynthesis.cancel();
    };
  }, [indicePreguntaActual, sesionEntrevista?.id, estadoVoz.habilitada, usuarioActual]);

  const perfilResumen = useMemo(
    () => ({
      sesionesCompletadas: historial.length,
      promedioPuntaje: historial.length ? Math.round(historial.reduce((acumulado, item) => acumulado + item.puntajeTotal, 0) / historial.length) : 0,
      tonoProfesional: describirProfesion(formularioConfiguracion.profesion),
      credencialesDemo: obtenerCredencialesDemo()
    }),
    [historial, formularioConfiguracion.profesion]
  );

  function actualizarIngreso(campo: keyof ValoresIngreso, valor: string) {
    setFormularioIngreso((anterior) => ({ ...anterior, [campo]: valor }));
  }

  function actualizarRegistro(campo: keyof ValoresRegistro, valor: string) {
    setFormularioRegistro((anterior) => ({ ...anterior, [campo]: valor }));
  }

  function actualizarConfiguracion(campo: keyof ValoresConfiguracion, valor: string) {
    setFormularioConfiguracion((anterior) => ({ ...anterior, [campo]: valor }));
  }

  async function manejarIngreso() {
    setCargandoAutenticacion(true);
    setErrorAutenticacion(undefined);

    try {
      if (!formularioIngreso.usuario.trim() || formularioIngreso.contrasena.length < 8) {
        throw new Error("Completa el usuario y una contrasena de minimo 8 caracteres.");
      }

      const sesion = await ingresarUsuario(formularioIngreso);
      await guardarSesionSegura(sesion);
      setTokenSesion(sesion.token);
      setUsuarioActual({ usuario: sesion.usuario, correo: sesion.correo });
      setHistorial(await obtenerHistorial(sesion.usuario, sesion.token));
      return true;
    } catch (error) {
      setErrorAutenticacion(error instanceof Error ? error.message : "No fue posible ingresar.");
      return false;
    } finally {
      setCargandoAutenticacion(false);
    }
  }

  async function manejarRegistro() {
    setCargandoAutenticacion(true);
    setErrorAutenticacion(undefined);

    try {
      if (formularioRegistro.contrasena !== formularioRegistro.confirmarContrasena) {
        throw new Error("Las contrasenas no coinciden.");
      }
      if (!formularioRegistro.correo.includes("@")) {
        throw new Error("Ingresa un correo valido.");
      }

      await registrarUsuario(formularioRegistro);
      setModoAutenticacion("ingreso");
      setFormularioIngreso({
        usuario: formularioRegistro.usuario,
        contrasena: formularioRegistro.contrasena
      });
      setErrorAutenticacion("Registro completado. Ahora puedes ingresar.");
    } catch (error) {
      setErrorAutenticacion(error instanceof Error ? error.message : "No fue posible registrar el usuario.");
    } finally {
      setCargandoAutenticacion(false);
    }
  }

  function cerrarSesion() {
    limpiarSesionSegura();
    setTokenSesion(undefined);
    setUsuarioActual(undefined);
    setHistorial([]);
    setSesionEntrevista(undefined);
    setIndicePreguntaActual(0);
    setVistaActiva("inicio");
  }

  async function comenzarEntrevista() {
    if (!usuarioActual) {
      setErrorAutenticacion("Debes ingresar antes de iniciar la entrevista.");
      return;
    }

    const sesion = await iniciarEntrevista(formularioConfiguracion, usuarioActual.usuario);
    setSesionEntrevista(sesion);
    setIndicePreguntaActual(0);
    referenciaPreguntaLeida.current = "";
    setVistaActiva("entrevista");
  }

  function repetirPregunta() {
    if (!sesionEntrevista || typeof window === "undefined" || !("speechSynthesis" in window)) {
      return;
    }

    const pregunta = sesionEntrevista.preguntas[indicePreguntaActual];
    if (!pregunta) {
      return;
    }

    hablarTexto(pregunta.enunciado);
  }

  function alternarVoz() {
    setEstadoVoz((anterior) => {
      if (anterior.habilitada && typeof window !== "undefined" && "speechSynthesis" in window) {
        window.speechSynthesis.cancel();
      }
      return { habilitada: !anterior.habilitada, estaHablando: false };
    });
  }

  async function iniciarGrabacion() {
    if (!estadoGrabadora.esCompatible || estadoGrabadora.estaGrabando) {
      return;
    }

    const flujo = await navigator.mediaDevices.getUserMedia({ audio: true });
    const grabadora = new MediaRecorder(flujo);
    referenciaGrabadora.current = grabadora;
    referenciaFragmentos.current = [];
    referenciaAutoEnvio.current = false;
    referenciaTranscripcion.current = "";

    const ConstructorReconocimiento = typeof window !== "undefined" ? window.SpeechRecognition || window.webkitSpeechRecognition : undefined;
    if (ConstructorReconocimiento) {
      const reconocimiento = new ConstructorReconocimiento();
      reconocimiento.lang = "es-ES";
      reconocimiento.continuous = true;
      reconocimiento.interimResults = true;
      reconocimiento.onresult = (evento) => {
        let texto = "";
        for (let indice = evento.resultIndex; indice < evento.results.length; indice += 1) {
          texto += evento.results[indice][0].transcript;
        }
        referenciaTranscripcion.current = texto.trim();
        setEstadoGrabadora((anterior) => ({
          ...anterior,
          transcripcionActual: referenciaTranscripcion.current
        }));
      };
      reconocimiento.onend = () => {
        referenciaReconocimiento.current = null;
      };
      referenciaReconocimiento.current = reconocimiento;
      reconocimiento.start();
    }

    grabadora.ondataavailable = (evento) => {
      if (evento.data.size > 0) {
        referenciaFragmentos.current.push(evento.data);
      }
    };

    grabadora.start();
    setEstadoGrabadora((anterior) => ({
      ...anterior,
      estaGrabando: true,
      duracionActual: 0,
      audioActualUrl: undefined,
      transcripcionActual: ""
    }));

    referenciaTemporizador.current = window.setInterval(() => {
      setEstadoGrabadora((anterior) => {
        const nuevaDuracion = anterior.duracionActual + 1;
        return {
          ...anterior,
          duracionActual: nuevaDuracion
        };
      });
    }, 1000);
  }

  async function enviarRespuesta() {
    if (!referenciaGrabadora.current || !sesionEntrevista || !usuarioActual) {
      return;
    }

    const preguntaActual = sesionEntrevista.preguntas[indicePreguntaActual];
    if (!preguntaActual) {
      return;
    }

    const duracion = estadoGrabadora.duracionActual;
    const grabadora = referenciaGrabadora.current;

    const audioUrl = await new Promise<string>((resolve) => {
      grabadora.onstop = () => {
        const blob = new Blob(referenciaFragmentos.current, { type: "audio/webm" });
        resolve(URL.createObjectURL(blob));
        grabadora.stream.getTracks().forEach((track) => track.stop());
      };
      grabadora.stop();
    });

    if (referenciaTemporizador.current) {
      window.clearInterval(referenciaTemporizador.current);
      referenciaTemporizador.current = null;
    }
    referenciaAutoEnvio.current = false;
    if (referenciaReconocimiento.current) {
      referenciaReconocimiento.current.stop();
      referenciaReconocimiento.current = null;
    }

    const preguntaEvaluada = await evaluarRespuesta({
      sesion: sesionEntrevista,
      preguntaActual,
      duracionSegundos: duracion,
      audioUrl,
      transcripcion: referenciaTranscripcion.current
    });

    setSesionEntrevista((anterior) => {
      if (!anterior) {
        return anterior;
      }

      return {
        ...anterior,
        preguntas: anterior.preguntas.map((pregunta) => (pregunta.id === preguntaEvaluada.id ? preguntaEvaluada : pregunta))
      };
    });

    setEstadoGrabadora((anterior) => ({
      ...anterior,
      estaGrabando: false,
      duracionActual: 0,
      audioActualUrl: audioUrl,
      transcripcionActual: referenciaTranscripcion.current
    }));

    const esUltima = indicePreguntaActual >= sesionEntrevista.preguntas.length - 1;
    if (!esUltima) {
      referenciaPreguntaLeida.current = "";
      window.setTimeout(() => {
        setIndicePreguntaActual((anterior) => anterior + 1);
        setEstadoGrabadora((anterior) => ({
          ...anterior,
          audioActualUrl: undefined,
          duracionActual: 0,
          transcripcionActual: ""
        }));
      }, 500);
      return;
    }

    const sesionActualizada = {
      ...sesionEntrevista,
      preguntas: sesionEntrevista.preguntas.map((pregunta) => (pregunta.id === preguntaEvaluada.id ? preguntaEvaluada : pregunta))
    };

    const sesionCerrada = await cerrarEntrevista({
      sesion: sesionActualizada,
      usuario: usuarioActual.usuario
    });

    setSesionEntrevista(sesionCerrada);
    setHistorial(await obtenerHistorial(usuarioActual.usuario, tokenSesion));
    setVistaActiva("inicio");
  }

  async function siguientePregunta() {
    if (!sesionEntrevista || !usuarioActual) {
      return;
    }

    const esUltima = indicePreguntaActual >= sesionEntrevista.preguntas.length - 1;
    if (!esUltima) {
      setIndicePreguntaActual((anterior) => anterior + 1);
      setEstadoGrabadora((anterior) => ({ ...anterior, audioActualUrl: undefined, duracionActual: 0 }));
      return;
    }

    const sesionCerrada = await cerrarEntrevista({
      sesion: sesionEntrevista,
      usuario: usuarioActual.usuario
    });

    setSesionEntrevista(sesionCerrada);
    setHistorial(await obtenerHistorial(usuarioActual.usuario, tokenSesion));
    setVistaActiva("inicio");
  }

  function anularEntrevista() {
    if (referenciaTemporizador.current) {
      window.clearInterval(referenciaTemporizador.current);
      referenciaTemporizador.current = null;
    }

    if (referenciaGrabadora.current?.state === "recording") {
      referenciaGrabadora.current.stream.getTracks().forEach((track) => track.stop());
      referenciaGrabadora.current.stop();
    }
    if (referenciaReconocimiento.current) {
      referenciaReconocimiento.current.stop();
      referenciaReconocimiento.current = null;
    }

    setSesionEntrevista(undefined);
    setIndicePreguntaActual(0);
    referenciaPreguntaLeida.current = "";
    referenciaAutoEnvio.current = false;
    setEstadoGrabadora((anterior) => ({
      ...anterior,
      estaGrabando: false,
      duracionActual: 0,
      audioActualUrl: undefined,
      transcripcionActual: ""
    }));
    setVistaActiva("inicio");
  }

  useEffect(() => {
    if (!estadoGrabadora.estaGrabando || estadoGrabadora.duracionActual < 40 || referenciaAutoEnvio.current) {
      return;
    }

    referenciaAutoEnvio.current = true;
    enviarRespuesta();
  }, [estadoGrabadora.estaGrabando, estadoGrabadora.duracionActual]);

  return {
    modoAutenticacion,
    setModoAutenticacion,
    vistaActiva,
    setVistaActiva,
    formularioIngreso,
    formularioRegistro,
    formularioConfiguracion,
    cargandoAutenticacion,
    errorAutenticacion,
    usuarioActual,
    historial,
    sesionEntrevista,
    indicePreguntaActual,
    estadoVoz,
    estadoGrabadora,
    sesionCargada,
    perfilResumen,
    actualizarIngreso,
    actualizarRegistro,
    actualizarConfiguracion,
    manejarIngreso,
    manejarRegistro,
    cerrarSesion,
    comenzarEntrevista,
    repetirPregunta,
    alternarVoz,
    iniciarGrabacion,
    enviarRespuesta,
    siguientePregunta,
    anularEntrevista
  };
}
