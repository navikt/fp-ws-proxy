# fp-ws-proxy

Stateless proxy for calls to external web services providing SOAP interfaces.

## Shared context

- Source of truth for shared domain, architecture, and conventions: `navikt/fp-context`
- Copilot Space: `navikt/TeamForeldrepenger`

## Repo-specific context

| Topic              | Details                                                                                    |
|--------------------|--------------------------------------------------------------------------------------------|
| Role               | Bridges FP services to SOAP-based targets                                                  |
| Consumers          | `fpoppdrag`, `fptilbake`, `fp-abakus` and some K9 applications                             |
| Tech stack        | Non-standard stateless backend; Spring Boot on Nais;                                       |

Integrations: 
- Arena / meldekortutbetalingsgrunnlag: vedtak for dagpenger and arbeidsavklaringspenger
- OS / simulering: simulate effect of the current behandling before sending oppdrag - identify potential repayment
- OS / tilbakekreving: send tilbakekreving vedtak

Deviations from standard FP tech stack apart from Spring Boot: 
- Nav `token-validation-spring` for incoming requests
- Code generation from WSDL
- Apache CXF using tokens from `securitytokenservice` for outgoing SOAP

## Entry points

- `ArenaController`: interface for meldekortutbetalingsgrunnlag, consumed by `fp-abakus`
- `SimuleringController`: interface for simulering, consumed by `fpoppdrag`
- `TilbakekrevingController`: interface for tilbakekreving, consumed by `fptilbake`

## Verification

- Manual verification on deploy. `fp-autotest` is not relevant (`vtp` mocks this app).
