"use client";

interface PropiedadesAvatarVirtual {
  hablando: boolean;
  tamano?: "grande" | "mediano";
}

export function AvatarVirtual({ hablando, tamano = "grande" }: PropiedadesAvatarVirtual) {
  return (
    <div className={`avatar-premium avatar-${tamano} ${hablando ? "hablando" : ""}`}>
      <div className="avatar-premium-escena">
        <div className="avatar-aura" />
        <div className="avatar-cuello" />
        <div className="avatar-cuerpo" />
        <div className="avatar-cabello avatar-cabello-atras" />
        <div className="avatar-cara">
          <div className="avatar-cejas">
            <span />
            <span />
          </div>
          <div className="avatar-ojos">
            <span className="avatar-ojo" />
            <span className="avatar-ojo" />
          </div>
          <div className="avatar-nariz" />
          <div className="avatar-boca">
            <span className="avatar-labio avatar-labio-superior" />
            <span className="avatar-labio avatar-labio-inferior" />
          </div>
        </div>
        <div className="avatar-cabello avatar-cabello-frente" />
      </div>
    </div>
  );
}
