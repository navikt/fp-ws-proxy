package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

public enum KodeAksjon {
    FINN_GRUNNLAG_OMGJØRING("3"),
    HENT_KORRIGERT_KRAVGRUNNLAG("4"),
    HENT_GRUNNLAG_OMGJØRING("5"),
    FATTE_VEDTAK("8"),
    ANNULERE_GRUNNLAG("A");

    private String kode;

    KodeAksjon(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
