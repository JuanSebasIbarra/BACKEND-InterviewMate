"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { AuthPanel } from "@/components/auth-panel";
import { useInterviewApp } from "@/hooks/useInterviewApp";

export default function HomePage() {
  const router = useRouter();
  const aplicacion = useInterviewApp();

  useEffect(() => {
    if (aplicacion.sesionCargada && aplicacion.usuarioActual) {
      router.replace("/inicio");
    }
  }, [aplicacion.sesionCargada, aplicacion.usuarioActual, router]);

  return (
    <main className="page-shell page-shell-login">
      <section className="login-grid">
        <div className="login-lateral">
          <div className="login-lateral-circulo" />
          <div className="login-lateral-contenido">
            <span className="mini-badge">Entrevistas con IA</span>
            <h2>Practica con preguntas reales, voz guiada y retroalimentación por pregunta.</h2>
          </div>
        </div>
        <AuthPanel
          modo={aplicacion.modoAutenticacion}
          formularioIngreso={aplicacion.formularioIngreso}
          formularioRegistro={aplicacion.formularioRegistro}
          errorAutenticacion={aplicacion.errorAutenticacion}
          cargandoAutenticacion={aplicacion.cargandoAutenticacion}
          cambiarModo={aplicacion.setModoAutenticacion}
          cambiarIngreso={aplicacion.actualizarIngreso}
          cambiarRegistro={aplicacion.actualizarRegistro}
          enviarIngreso={async () => {
            const exito = await aplicacion.manejarIngreso();
            if (exito) {
              router.push("/inicio");
            }
          }}
          enviarRegistro={aplicacion.manejarRegistro}
        />
      </section>
    </main>
  );
}
