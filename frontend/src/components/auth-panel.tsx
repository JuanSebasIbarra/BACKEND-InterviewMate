import { ModoAutenticacion, ValoresIngreso, ValoresRegistro } from "@/types";

interface PropiedadesAuthPanel {
  modo: ModoAutenticacion;
  formularioIngreso: ValoresIngreso;
  formularioRegistro: ValoresRegistro;
  errorAutenticacion?: string;
  cargandoAutenticacion: boolean;
  cambiarModo: (modo: ModoAutenticacion) => void;
  cambiarIngreso: (campo: keyof ValoresIngreso, valor: string) => void;
  cambiarRegistro: (campo: keyof ValoresRegistro, valor: string) => void;
  enviarIngreso: () => void;
  enviarRegistro: () => void;
}

export function AuthPanel({
  modo,
  formularioIngreso,
  formularioRegistro,
  errorAutenticacion,
  cargandoAutenticacion,
  cambiarModo,
  cambiarIngreso,
  cambiarRegistro,
  enviarIngreso,
  enviarRegistro
}: PropiedadesAuthPanel) {
  return (
    <aside className="panel auth-panel auth-panel-limpio">
      <div className="auth-encabezado">
        <span className="auth-topline auth-topline-oscuro">InterviewMate</span>
        <h1 className="headline headline-claro">
          Acceso claro,
          <br />
          <span>experiencia profesional.</span>
        </h1>
        <p className="subcopy subcopy-claro">
          Inicia sesion o registra tu cuenta para acceder al simulador de entrevistas con avatar, audio en tiempo real e historial con recomendaciones.
        </p>
      </div>

      <div>
        <div className="tab-row tab-row-claro" role="tablist" aria-label="Modo de autenticacion">
          <button className={`tab-button ${modo === "ingreso" ? "active" : ""}`} type="button" onClick={() => cambiarModo("ingreso")}>
            Login
          </button>
          <button className={`tab-button ${modo === "registro" ? "active" : ""}`} type="button" onClick={() => cambiarModo("registro")}>
            Registro
          </button>
        </div>

        {modo === "ingreso" ? (
          <div className="form-shell">
            <div className="field-grid">
              <div className="field">
                <label htmlFor="usuario-ingreso">Usuario</label>
                <input id="usuario-ingreso" value={formularioIngreso.usuario} onChange={(event) => cambiarIngreso("usuario", event.target.value)} placeholder="Ingresa tu usuario" />
              </div>
              <div className="field">
                <label htmlFor="contrasena-ingreso">Contrasena</label>
                <input
                  id="contrasena-ingreso"
                  type="password"
                  value={formularioIngreso.contrasena}
                  onChange={(event) => cambiarIngreso("contrasena", event.target.value)}
                  placeholder="Minimo 8 caracteres"
                />
              </div>
            </div>
            {errorAutenticacion ? <span className="error-text">{errorAutenticacion}</span> : null}
            <button className="primary-button primary-button-azul" type="button" disabled={cargandoAutenticacion} onClick={enviarIngreso}>
              {cargandoAutenticacion ? "Ingresando..." : "Entrar al sistema"}
            </button>
          </div>
        ) : (
          <div className="form-shell">
            <div className="field-grid">
              <div className="field">
                <label htmlFor="usuario-registro">Usuario</label>
                <input id="usuario-registro" value={formularioRegistro.usuario} onChange={(event) => cambiarRegistro("usuario", event.target.value)} />
              </div>
              <div className="field">
                <label htmlFor="correo-registro">Correo</label>
                <input id="correo-registro" type="email" value={formularioRegistro.correo} onChange={(event) => cambiarRegistro("correo", event.target.value)} />
              </div>
              <div className="field-row">
                <div className="field">
                  <label htmlFor="contrasena-registro">Contrasena</label>
                  <input
                    id="contrasena-registro"
                    type="password"
                    value={formularioRegistro.contrasena}
                    onChange={(event) => cambiarRegistro("contrasena", event.target.value)}
                  />
                </div>
                <div className="field">
                  <label htmlFor="confirmar-registro">Confirmar contrasena</label>
                  <input
                    id="confirmar-registro"
                    type="password"
                    value={formularioRegistro.confirmarContrasena}
                    onChange={(event) => cambiarRegistro("confirmarContrasena", event.target.value)}
                  />
                </div>
              </div>
            </div>
            {errorAutenticacion ? <span className="error-text">{errorAutenticacion}</span> : null}
            <button className="primary-button primary-button-azul" type="button" disabled={cargandoAutenticacion} onClick={enviarRegistro}>
              {cargandoAutenticacion ? "Creando cuenta..." : "Crear cuenta"}
            </button>
          </div>
        )}
      </div>
    </aside>
  );
}
