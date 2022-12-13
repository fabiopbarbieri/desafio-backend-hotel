--
-- TABLES
--

CREATE TABLE IF NOT EXISTS "hospedes" (
	"documento" VARCHAR(14) NOT NULL,
	"nome" VARCHAR(255) NULL DEFAULT NULL,
	"telefone" VARCHAR(20) NULL DEFAULT NULL,
	"gastos_documento" VARCHAR(255) NULL DEFAULT NULL,
	PRIMARY KEY ("documento")
	--,CONSTRAINT "fk_gastos_hospedes" FOREIGN KEY ("gastos_documento") REFERENCES "gastos_hospede" ("documento") ON UPDATE NO ACTION ON DELETE NO ACTION
);

--
--
--

CREATE TABLE IF NOT EXISTS "registro_hospedagem" (
	"id" INTEGER NOT NULL,
	"adicional_veiculo" BOOLEAN NOT NULL,
	"data_entrada" TIMESTAMP NOT NULL,
	"data_saida" TIMESTAMP NOT NULL,
	"valor_hospedagem" DOUBLE PRECISION NOT NULL,
	"documento_hospede" VARCHAR(255) NOT NULL,
	PRIMARY KEY ("id"),
	CONSTRAINT "fk_hospedes" FOREIGN KEY ("documento_hospede") REFERENCES "hospedes" ("documento") ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- 
-- VIEWS
--

CREATE OR REPLACE VIEW "gastos_hospedes" AS SELECT h.documento, COALESCE(uh.valor_ultima_hospedagem, 0) "valor_ultima_hospedagem", COALESCE(th.valor_total_hospedagens,0) "valor_total_hospedagens" 
FROM hospedes h 
LEFT JOIN LATERAL (
SELECT rh.valor_hospedagem "valor_ultima_hospedagem"
FROM registro_hospedagem rh
WHERE rh.documento_hospede = h.documento
ORDER BY rh.data_entrada DESC
LIMIT 1) uh ON TRUE
LEFT JOIN LATERAL (
SELECT SUM(rh.valor_hospedagem) AS "valor_total_hospedagens"
FROM registro_hospedagem rh
WHERE rh.documento_hospede = h.documento) th ON TRUE;
