import { obtenerCargosPorProfesion, opcionesNivel, opcionesProfesion } from "@/data/catalog";
import { AvatarVirtual } from "@/components/avatar-virtual";
import { formatearFecha, formatearPorcentaje, obtenerIniciales } from "@/lib/utils";
import {
  EstadoGrabadora,
  EstadoVoz,
  HistorialEntrevista,
  PerfilUsuario,
  SesionEntrevista,
  ValoresConfiguracion,
  VistaActiva
} from "@/types";

interface PropiedadesDashboard {
  usuario?: PerfilUsuario;
  perfilResumen: {
    sesionesCompletadas: number;
    promedioPuntaje: number;
    tonoProfesional: string;
  };
  historial: HistorialEntrevista[];
  vistaActiva: VistaActiva;
  formularioConfiguracion: ValoresConfiguracion;
  sesionEntrevista?: SesionEntrevista;
  indicePreguntaActual: number;
  estadoGrabadora: EstadoGrabadora;
  estadoVoz: EstadoVoz;
  cambiarVista: (vista: VistaActiva) => void;
  actualizarConfiguracion: (campo: keyof ValoresConfiguracion, valor: string) => void;
  cerrarSesion: () => void;
  comenzarEntrevista: () => void;
  alternarVoz: () => void;
  iniciarGrabacion: () => void;
  enviarRespuesta: () => void;
  siguientePregunta: () => void;
  repetirPregunta: () => void;
  anularEntrevista: () => void;
}

export function DashboardShell({
  usuario,
  perfilResumen,
  historial,
  vistaActiva,
  formularioConfiguracion,
  sesionEntrevista,
  indicePreguntaActual,
  estadoGrabadora,
  estadoVoz,
  cambiarVista,
  actualizarConfiguracion,
  cerrarSesion,
  comenzarEntrevista,
  alternarVoz,
  iniciarGrabacion,
  enviarRespuesta,
  siguientePregunta,
  repetirPregunta,
  anularEntrevista
}: PropiedadesDashboard) {
  const preguntaActual = sesionEntrevista?.preguntas[indicePreguntaActual];
  const cargos = obtenerCargosPorProfesion(formularioConfiguracion.profesion);

  return (
    <section className="panel dashboard-panel dashboard-panel-claro">
      <div className="dashboard-header">
        <span className="mini-badge mini-badge-claro">Panel de inicio</span>
        <button className="ghost-button ghost-button-claro" type="button" onClick={cerrarSesion}>
          Cerrar sesión
        </button>
      </div>

      <div className="dashboard-nav">
        <button className={`nav-button ${vistaActiva === "inicio" ? "active" : ""}`} type="button" onClick={() => cambiarVista("inicio")}>
          Inicio
        </button>
        <button className={`nav-button ${vistaActiva === "entrevista" ? "active" : ""}`} type="button" onClick={() => cambiarVista("entrevista")}>
          Entrevista
        </button>
      </div>

      <div className="dashboard-grid">
        <div className="glass-card glass-card-claro">
          <div className="dashboard-header">
            <div>
              <small className="muted-text">Bienvenido</small>
              <h2 style={{ margin: "6px 0 0", fontSize: "2rem", color: "#081019" }}>
                {usuario ? `${usuario.usuario}, prepárate para tu próxima entrevista.` : "Panel principal"}
              </h2>
            </div>
            <div className="avatar-bust avatar-bust-claro">{obtenerIniciales(usuario?.usuario)}</div>
          </div>
          <div className="stats-grid">
            <div className="stat-card stat-card-claro">
              <strong>Sesiones</strong>
              <span>{perfilResumen.sesionesCompletadas}</span>
            </div>
            <div className="stat-card stat-card-claro">
              <strong>Promedio</strong>
              <span>{perfilResumen.promedioPuntaje}%</span>
            </div>
            <div className="stat-card stat-card-claro">
              <strong>Enfoque</strong>
              <span>{perfilResumen.tonoProfesional}</span>
            </div>
          </div>
        </div>

        <div className="glass-card glass-card-claro avatar-real-card">
          <div className="interview-header">
            <div>
              <strong style={{ color: "#081019" }}>Avatar IA</strong>
              <p className="subcopy subcopy-oscuro">Este avatar leerá las preguntas en español y abrirá la entrevista con un saludo personalizado.</p>
            </div>
          </div>
          <div className="avatar-real-contenedor">
            <AvatarVirtual hablando={estadoVoz.estaHablando} tamano="mediano" />
          </div>
          <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
            <button className="secondary-button secondary-button-claro" type="button" onClick={alternarVoz}>
              {estadoVoz.habilitada ? "Silenciar avatar" : "Activar avatar"}
            </button>
            <button className="ghost-button ghost-button-claro" type="button" onClick={() => cambiarVista("entrevista")}>
              Abrir entrevista
            </button>
          </div>
        </div>
      </div>

      {vistaActiva === "inicio" ? (
        <div className="dashboard-grid">
          <div className="setup-card setup-card-claro">
            <div className="interview-header">
              <div>
                <strong style={{ color: "#081019" }}>Requisitos de la entrevista</strong>
                <p className="subcopy subcopy-oscuro">Selecciona nivel, profesión y cargo. Esta información será enviada para generar la entrevista en tiempo real.</p>
              </div>
            </div>
            <div className="field-grid">
              <div className="field-row">
                <div className="field">
                  <label htmlFor="nivel">Módulo 1. Nivel</label>
                  <select id="nivel" value={formularioConfiguracion.nivel} onChange={(event) => actualizarConfiguracion("nivel", event.target.value)}>
                    {opcionesNivel.map((nivel) => (
                      <option key={nivel} value={nivel}>
                        Nivel {nivel}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="field">
                  <label htmlFor="profesion">Módulo 2. Profesión</label>
                  <select id="profesion" value={formularioConfiguracion.profesion} onChange={(event) => actualizarConfiguracion("profesion", event.target.value)}>
                    {opcionesProfesion.map((item) => (
                      <option key={item.profesion} value={item.profesion}>
                        {item.profesion}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="field-row">
                <div className="field">
                  <label htmlFor="cargo">Módulo 3. Cargo</label>
                  <select id="cargo" value={formularioConfiguracion.cargo} onChange={(event) => actualizarConfiguracion("cargo", event.target.value)}>
                    {cargos.map((cargo) => (
                      <option key={cargo} value={cargo}>
                        {cargo}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="field">
                  <label htmlFor="empresa">Empresa</label>
                  <input id="empresa" value={formularioConfiguracion.empresa} onChange={(event) => actualizarConfiguracion("empresa", event.target.value)} />
                </div>
              </div>
            </div>
            <div style={{ marginTop: 18 }}>
              <button className="primary-button primary-button-azul" type="button" onClick={comenzarEntrevista}>
                Comenzar entrevista
              </button>
            </div>
          </div>

          <div className="glass-card glass-card-claro">
            <div className="interview-header">
              <div>
                <strong style={{ color: "#081019" }}>Historial y recomendaciones</strong>
                <p className="subcopy subcopy-oscuro">Aquí verás tus sesiones realizadas y las recomendaciones por cada pregunta.</p>
              </div>
            </div>
            {historial.length ? (
              <div className="history-list">
                {historial.map((item) => (
                  <div className="history-card history-card-claro" key={item.id}>
                    <div className="history-meta">
                      <div>
                        <strong>{item.titulo}</strong>
                        <small>{item.subtitulo}</small>
                      </div>
                      <span className="score-chip score-chip-claro">{item.puntajeTotal}%</span>
                    </div>
                    <div className="score-row">
                      <small>{formatearFecha(item.fecha)}</small>
                      <small>Nivel {item.nivel}</small>
                    </div>
                    <div className="feedback-list" style={{ marginTop: 12 }}>
                      {item.recomendacionesPorPregunta.map((recomendacion, indice) => (
                        <div className="feedback-card feedback-card-claro" key={`${item.id}-${indice}`}>
                          <strong>Pregunta {indice + 1}</strong>
                          <span>{recomendacion.recomendacion}</span>
                          <small>Puntaje: {recomendacion.puntaje}%</small>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state empty-state-claro">Todavía no hay historial. Completa una entrevista para ver tus recomendaciones.</div>
            )}
          </div>
        </div>
      ) : (
        <div className="glass-card glass-card-claro">
          {!sesionEntrevista || !preguntaActual ? (
            <div className="empty-state empty-state-claro">Inicia una entrevista desde la vista de inicio para comenzar.</div>
          ) : (
            <>
              <div className="avatar-entrevista-central">
                <div className="avatar-entrevista-marco">
                  <AvatarVirtual hablando={estadoVoz.estaHablando} tamano="grande" />
                </div>
              </div>

              <div className="interview-header">
                <div>
                  <strong style={{ color: "#081019" }}>Entrevista en curso</strong>
                  <p className="subcopy subcopy-oscuro">
                    {sesionEntrevista.profesion} | {sesionEntrevista.cargo} | Nivel {sesionEntrevista.nivel}
                  </p>
                </div>
                <button className="ghost-button ghost-button-claro" type="button" onClick={anularEntrevista}>
                  Anular entrevista
                </button>
              </div>

              <div className="progress-bar" aria-hidden="true">
                <div className="progress-value" style={{ width: `${((indicePreguntaActual + 1) / sesionEntrevista.preguntas.length) * 100}%` }} />
              </div>

              <div className="question-card question-card-claro" style={{ marginTop: 18 }}>
                <div className="score-row">
                  <span className="mini-badge mini-badge-claro">Pregunta {preguntaActual.orden} de 10</span>
                  <small>Voz {estadoVoz.habilitada ? "activa" : "silenciada"}</small>
                </div>
                <h3 style={{ color: "#081019" }}>{preguntaActual.enunciado}</h3>
                <p className="question-meta question-meta-claro">El usuario responde con audio, lo envía y continúa con la siguiente pregunta.</p>
              </div>

              <div className="audio-controls" style={{ marginTop: 18, flexWrap: "wrap" }}>
                <button className={`record-button record-button-claro ${estadoGrabadora.estaGrabando ? "recording" : ""}`} type="button" disabled={!estadoGrabadora.esCompatible || estadoGrabadora.estaGrabando} onClick={iniciarGrabacion}>
                  {estadoGrabadora.estaGrabando ? "Grabando..." : "Grabar respuesta"}
                </button>
                <button className="secondary-button secondary-button-claro" type="button" disabled={!estadoGrabadora.estaGrabando} onClick={enviarRespuesta}>
                  Enviar audio
                </button>
                <button className="ghost-button ghost-button-claro" type="button" onClick={repetirPregunta}>
                  Repetir pregunta
                </button>
              </div>

              <small className="muted-text">
                Duración actual del audio: {estadoGrabadora.duracionActual} segundos. Límite máximo: 40 segundos. Al enviarse el audio la entrevista avanzará automáticamente.
              </small>
              {estadoGrabadora.reconocimientoDisponible ? (
                <small className="muted-text">
                  Transcripción en vivo: {estadoGrabadora.transcripcionActual || "esperando voz..."}
                </small>
              ) : (
                <small className="muted-text">
                  Tu navegador no ofrece transcripción local automática. La nota se basará principalmente en duración y estructura.
                </small>
              )}

              {preguntaActual.audioUrl ? (
                <div className="question-feedback" style={{ marginTop: 18 }}>
                  <audio controls src={preguntaActual.audioUrl} />
                  <div className="feedback-card feedback-card-claro">
                    <div className="history-meta">
                      <strong>Evaluación de la IA</strong>
                      <span className="score-chip score-chip-claro">{formatearPorcentaje(preguntaActual.puntaje)}</span>
                    </div>
                    <span>{preguntaActual.retroalimentacion}</span>
                    <span className="muted-text">{preguntaActual.recomendacion}</span>
                    {preguntaActual.transcripcion ? <span className="muted-text">Transcripción detectada: {preguntaActual.transcripcion}</span> : null}
                  </div>
                </div>
              ) : null}
            </>
          )}
        </div>
      )}
    </section>
  );
}
