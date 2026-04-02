"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { DashboardShell } from "@/components/dashboard-shell";
import { useInterviewApp } from "@/hooks/useInterviewApp";

export default function InicioPage() {
  const router = useRouter();
  const aplicacion = useInterviewApp();

  useEffect(() => {
    if (aplicacion.sesionCargada && !aplicacion.usuarioActual) {
      router.replace("/");
    }
  }, [aplicacion.sesionCargada, aplicacion.usuarioActual, router]);

  if (!aplicacion.sesionCargada) {
    return <main className="page-shell"><div className="panel dashboard-panel">Cargando sesión...</div></main>;
  }

  return (
    <main className="page-shell page-shell-inicio">
      <DashboardShell
        usuario={aplicacion.usuarioActual}
        perfilResumen={aplicacion.perfilResumen}
        historial={aplicacion.historial}
        vistaActiva={aplicacion.vistaActiva}
        formularioConfiguracion={aplicacion.formularioConfiguracion}
        sesionEntrevista={aplicacion.sesionEntrevista}
        indicePreguntaActual={aplicacion.indicePreguntaActual}
        estadoGrabadora={aplicacion.estadoGrabadora}
        estadoVoz={aplicacion.estadoVoz}
        cambiarVista={aplicacion.setVistaActiva}
        actualizarConfiguracion={aplicacion.actualizarConfiguracion}
        cerrarSesion={() => {
          aplicacion.cerrarSesion();
          router.push("/");
        }}
        comenzarEntrevista={aplicacion.comenzarEntrevista}
        alternarVoz={aplicacion.alternarVoz}
        iniciarGrabacion={aplicacion.iniciarGrabacion}
        enviarRespuesta={aplicacion.enviarRespuesta}
        siguientePregunta={aplicacion.siguientePregunta}
        repetirPregunta={aplicacion.repetirPregunta}
        anularEntrevista={aplicacion.anularEntrevista}
      />
    </main>
  );
}
