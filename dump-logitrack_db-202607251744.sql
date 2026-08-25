--
-- PostgreSQL database cluster dump
--

-- Started on 2026-07-25 17:44:45

\restrict Xl0ZtavbF3QfhjF9SkZgSpd523436qFTg9BgC6UOUxigsc4jmHHRm7fGiTPIbEL

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE anon;
ALTER ROLE anon WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE authenticated;
ALTER ROLE authenticated WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE authenticator;
ALTER ROLE authenticator WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE dashboard_user;
ALTER ROLE dashboard_user WITH NOSUPERUSER INHERIT CREATEROLE CREATEDB NOLOGIN REPLICATION NOBYPASSRLS;
CREATE ROLE pgbouncer;
ALTER ROLE pgbouncer WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE postgres;
ALTER ROLE postgres WITH NOSUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS;
CREATE ROLE service_role;
ALTER ROLE service_role WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION BYPASSRLS;
CREATE ROLE supabase_admin;
ALTER ROLE supabase_admin WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS;
CREATE ROLE supabase_auth_admin;
ALTER ROLE supabase_auth_admin WITH NOSUPERUSER NOINHERIT CREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE supabase_etl_admin;
ALTER ROLE supabase_etl_admin WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN REPLICATION BYPASSRLS;
CREATE ROLE supabase_privileged_role;
ALTER ROLE supabase_privileged_role WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE supabase_read_only_user;
ALTER ROLE supabase_read_only_user WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION BYPASSRLS;
CREATE ROLE supabase_realtime_admin;
ALTER ROLE supabase_realtime_admin WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE supabase_replication_admin;
ALTER ROLE supabase_replication_admin WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN REPLICATION NOBYPASSRLS;
CREATE ROLE supabase_storage_admin;
ALTER ROLE supabase_storage_admin WITH NOSUPERUSER NOINHERIT CREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;

--
-- User Configurations
--

--
-- User Config "anon"
--

ALTER ROLE anon SET statement_timeout TO '3s';

--
-- User Config "authenticated"
--

ALTER ROLE authenticated SET statement_timeout TO '8s';

--
-- User Config "authenticator"
--

ALTER ROLE authenticator SET session_preload_libraries TO 'supautils', 'safeupdate';
ALTER ROLE authenticator SET statement_timeout TO '8s';
ALTER ROLE authenticator SET lock_timeout TO '8s';

--
-- User Config "postgres"
--

ALTER ROLE postgres SET search_path TO E'\\$user', 'public', 'extensions';

--
-- User Config "supabase_admin"
--

ALTER ROLE supabase_admin SET search_path TO '$user', 'public', 'auth', 'extensions';
ALTER ROLE supabase_admin SET log_statement TO 'none';
ALTER ROLE supabase_admin SET statement_timeout TO '0';

--
-- User Config "supabase_auth_admin"
--

ALTER ROLE supabase_auth_admin SET search_path TO 'auth';
ALTER ROLE supabase_auth_admin SET idle_in_transaction_session_timeout TO '60000';
ALTER ROLE supabase_auth_admin SET log_statement TO 'none';

--
-- User Config "supabase_read_only_user"
--

ALTER ROLE supabase_read_only_user SET default_transaction_read_only TO 'on';

--
-- User Config "supabase_storage_admin"
--

ALTER ROLE supabase_storage_admin SET search_path TO 'storage';
ALTER ROLE supabase_storage_admin SET log_statement TO 'none';


--
-- Role memberships
--

GRANT anon TO authenticator WITH INHERIT FALSE GRANTED BY supabase_admin;
GRANT anon TO postgres WITH ADMIN OPTION, INHERIT TRUE GRANTED BY supabase_admin;
GRANT authenticated TO authenticator WITH INHERIT FALSE GRANTED BY supabase_admin;
GRANT authenticated TO postgres WITH ADMIN OPTION, INHERIT TRUE GRANTED BY supabase_admin;
GRANT authenticator TO postgres WITH ADMIN OPTION, INHERIT TRUE GRANTED BY supabase_admin;
GRANT authenticator TO supabase_storage_admin WITH INHERIT FALSE GRANTED BY supabase_admin;
GRANT pg_create_subscription TO postgres WITH ADMIN OPTION, INHERIT TRUE GRANTED BY supabase_admin;
GRANT pg_monitor TO postgres WITH ADMIN OPTION, INHERIT TRUE GRANTED BY supabase_admin;
GRANT pg_monitor TO supabase_etl_admin WITH INHERIT TRUE GRANTED BY supabase_admin;
GRANT pg_monitor TO supabase_read_only_user WITH INHERIT TRUE GRANTED BY supabase_admin;
GRANT pg_read_all_data TO postgres WITH ADMIN OPTION, INHERIT TRUE GRANTED BY supabase_admin;
GRANT pg_read_all_data TO supabase_etl_admin WITH INHERIT TRUE GRANTED BY supabase_admin;
GRANT pg_read_all_data TO supabase_read_only_user WITH INHERIT TRUE GRANTED BY supabase_admin;
GRANT pg_signal_backend TO postgres WITH ADMIN OPTION, INHERIT TRUE GRANTED BY supabase_admin;
GRANT service_role TO authenticator WITH INHERIT FALSE GRANTED BY supabase_admin;
GRANT service_role TO postgres WITH ADMIN OPTION, INHERIT TRUE GRANTED BY supabase_admin;
GRANT supabase_privileged_role TO postgres WITH INHERIT TRUE GRANTED BY supabase_admin;
GRANT supabase_privileged_role TO supabase_etl_admin WITH INHERIT TRUE GRANTED BY supabase_admin;






\unrestrict Xl0ZtavbF3QfhjF9SkZgSpd523436qFTg9BgC6UOUxigsc4jmHHRm7fGiTPIbEL

--
-- Databases
--

--
-- Database "Test" dump
--

--
-- PostgreSQL database dump
--

\restrict UvnCrW7wWh9idcNkQqIQds4BWHBjELVrhRhiq0ytdEcYKIgr8VtLGscj3zH3Unm

-- Dumped from database version 17.6
-- Dumped by pg_dump version 18.4

-- Started on 2026-07-25 17:44:49

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3401 (class 1262 OID 17959)
-- Name: Test; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "Test" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = icu LOCALE = 'C.UTF-8' ICU_LOCALE = 'C.UTF-8';


ALTER DATABASE "Test" OWNER TO postgres;

\unrestrict UvnCrW7wWh9idcNkQqIQds4BWHBjELVrhRhiq0ytdEcYKIgr8VtLGscj3zH3Unm
\connect "Test"
\restrict UvnCrW7wWh9idcNkQqIQds4BWHBjELVrhRhiq0ytdEcYKIgr8VtLGscj3zH3Unm

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Completed on 2026-07-25 17:44:58

--
-- PostgreSQL database dump complete
--

\unrestrict UvnCrW7wWh9idcNkQqIQds4BWHBjELVrhRhiq0ytdEcYKIgr8VtLGscj3zH3Unm

--
-- Database "logitrack_db" dump
--

--
-- PostgreSQL database dump
--

\restrict NJkVWZAvaUPhCjxnutVrf8sNfQLc7plLnlJuZ5dJyJJU9wUXUchua4kkHeVm1tf

-- Dumped from database version 17.6
-- Dumped by pg_dump version 18.4

-- Started on 2026-07-25 17:44:58

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3462 (class 1262 OID 25429)
-- Name: logitrack_db; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE logitrack_db WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = icu LOCALE = 'en_US.UTF-8' ICU_LOCALE = 'en-US';


ALTER DATABASE logitrack_db OWNER TO postgres;

\unrestrict NJkVWZAvaUPhCjxnutVrf8sNfQLc7plLnlJuZ5dJyJJU9wUXUchua4kkHeVm1tf
\connect logitrack_db
\restrict NJkVWZAvaUPhCjxnutVrf8sNfQLc7plLnlJuZ5dJyJJU9wUXUchua4kkHeVm1tf

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 25442)
-- Name: logitrack; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA logitrack;


ALTER SCHEMA logitrack OWNER TO postgres;

--
-- TOC entry 868 (class 1247 OID 25527)
-- Name: rol_enum; Type: TYPE; Schema: logitrack; Owner: postgres
--

CREATE TYPE logitrack.rol_enum AS ENUM (
    'ADMIN',
    'EMPLEADO'
);


ALTER TYPE logitrack.rol_enum OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 227 (class 1259 OID 25540)
-- Name: auditorias; Type: TABLE; Schema: logitrack; Owner: postgres
--

CREATE TABLE logitrack.auditorias (
    id bigint NOT NULL,
    entidad_afectada character varying(255),
    id_entidad character varying(255),
    operacion character varying(255),
    usuario character varying(255),
    fecha timestamp without time zone
);


ALTER TABLE logitrack.auditorias OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 25539)
-- Name: auditorias_id_seq; Type: SEQUENCE; Schema: logitrack; Owner: postgres
--

CREATE SEQUENCE logitrack.auditorias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE logitrack.auditorias_id_seq OWNER TO postgres;

--
-- TOC entry 3463 (class 0 OID 0)
-- Dependencies: 226
-- Name: auditorias_id_seq; Type: SEQUENCE OWNED BY; Schema: logitrack; Owner: postgres
--

ALTER SEQUENCE logitrack.auditorias_id_seq OWNED BY logitrack.auditorias.id;


--
-- TOC entry 219 (class 1259 OID 25444)
-- Name: bodega; Type: TABLE; Schema: logitrack; Owner: postgres
--

CREATE TABLE logitrack.bodega (
    id bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    ubicacion character varying(225) NOT NULL,
    capacidad integer NOT NULL,
    encargado character varying(225) NOT NULL
);


ALTER TABLE logitrack.bodega OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 25443)
-- Name: bodega_id_seq; Type: SEQUENCE; Schema: logitrack; Owner: postgres
--

ALTER TABLE logitrack.bodega ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME logitrack.bodega_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 223 (class 1259 OID 25482)
-- Name: movimientos; Type: TABLE; Schema: logitrack; Owner: postgres
--

CREATE TABLE logitrack.movimientos (
    id bigint NOT NULL,
    tipo character varying(255) NOT NULL,
    cantidad integer NOT NULL,
    fecha_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    producto_id bigint NOT NULL,
    bodega_origen_id bigint,
    bodega_destino_id bigint,
    usuario_id bigint
);


ALTER TABLE logitrack.movimientos OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 25481)
-- Name: movimientos_id_seq; Type: SEQUENCE; Schema: logitrack; Owner: postgres
--

CREATE SEQUENCE logitrack.movimientos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE logitrack.movimientos_id_seq OWNER TO postgres;

--
-- TOC entry 3464 (class 0 OID 0)
-- Dependencies: 222
-- Name: movimientos_id_seq; Type: SEQUENCE OWNED BY; Schema: logitrack; Owner: postgres
--

ALTER SEQUENCE logitrack.movimientos_id_seq OWNED BY logitrack.movimientos.id;


--
-- TOC entry 221 (class 1259 OID 25464)
-- Name: productos; Type: TABLE; Schema: logitrack; Owner: postgres
--

CREATE TABLE logitrack.productos (
    id bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion character varying(255),
    precio numeric(10,2) NOT NULL,
    stock integer DEFAULT 0 NOT NULL,
    bodega_id bigint NOT NULL,
    categoria character varying(100)
);


ALTER TABLE logitrack.productos OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 25463)
-- Name: productos_id_seq; Type: SEQUENCE; Schema: logitrack; Owner: postgres
--

ALTER TABLE logitrack.productos ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME logitrack.productos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 225 (class 1259 OID 25505)
-- Name: usuarios; Type: TABLE; Schema: logitrack; Owner: postgres
--

CREATE TABLE logitrack.usuarios (
    id bigint NOT NULL,
    password character varying(255) NOT NULL,
    rol character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    CONSTRAINT usuarios_rol_check CHECK (((rol)::text = ANY (ARRAY[('ADMIN'::character varying)::text, ('EMPLEADO'::character varying)::text])))
);


ALTER TABLE logitrack.usuarios OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 25504)
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: logitrack; Owner: postgres
--

ALTER TABLE logitrack.usuarios ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME logitrack.usuarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 3283 (class 2604 OID 25543)
-- Name: auditorias id; Type: DEFAULT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.auditorias ALTER COLUMN id SET DEFAULT nextval('logitrack.auditorias_id_seq'::regclass);


--
-- TOC entry 3281 (class 2604 OID 25485)
-- Name: movimientos id; Type: DEFAULT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.movimientos ALTER COLUMN id SET DEFAULT nextval('logitrack.movimientos_id_seq'::regclass);


--
-- TOC entry 3456 (class 0 OID 25540)
-- Dependencies: 227
-- Data for Name: auditorias; Type: TABLE DATA; Schema: logitrack; Owner: postgres
--

COPY logitrack.auditorias (id, entidad_afectada, id_entidad, operacion, usuario, fecha) FROM stdin;
1	Producto	3	INSERT	admin1	2026-07-22 12:27:58.703761
2	Bodega	2	INSERT	admin1	2026-07-23 13:28:47.031554
3	Bodega	3	INSERT	admin1	2026-07-23 13:35:29.243138
4	Bodega	4	INSERT	admin1	2026-07-23 13:39:05.419609
5	Movimiento	3	INSERT	empleado1	2026-07-23 13:40:53.367585
6	Movimiento	28	INSERT	admin1	2026-07-23 23:04:10.436132
7	Producto	1	UPDATE	admin1	2026-07-23 23:04:10.741739
8	Movimiento	29	INSERT	admin1	2026-07-24 13:56:15.397977
9	Producto	1	UPDATE	admin1	2026-07-24 13:56:15.644982
10	Movimiento	30	INSERT	admin1	2026-07-24 13:56:54.961025
11	Producto	1	UPDATE	admin1	2026-07-24 13:56:55.187191
12	Movimiento	31	INSERT	admin1	2026-07-24 13:57:37.973931
13	Movimiento	32	INSERT	empleado1	2026-07-25 11:24:23.080314
14	Producto	4	UPDATE	empleado1	2026-07-25 11:24:23.442214
15	Movimiento	33	INSERT	empleado1	2026-07-25 12:05:01.707272
16	Movimiento	34	INSERT	empleado1	2026-07-25 13:15:38.584002
17	Producto	1	UPDATE	empleado1	2026-07-25 13:15:38.87924
18	Producto	3	DELETE	admin1	2026-07-25 17:32:03.196087
19	Movimiento	35	INSERT	admin1	2026-07-25 17:35:43.482497
\.


--
-- TOC entry 3448 (class 0 OID 25444)
-- Dependencies: 219
-- Data for Name: bodega; Type: TABLE DATA; Schema: logitrack; Owner: postgres
--

COPY logitrack.bodega (id, nombre, ubicacion, capacidad, encargado) FROM stdin;
1	Bodega Principal	Zona Norte	1000	Marcos Robles
2	Bodega Norte	Bogotá	500	Carlos Pérez
3	Bodega Norte	Bogotá	500	Carlos Pérez
4	Bodega Norte	Bogotá	500	Carlos Pérez
5	Bodega Principal (Central)	Zona Franca - Módulo 1	5000	Pedro Gómez
6	Bodega Secundaria (Norte)	Parque Industrial - Módulo 4	2500	María Rodríguez
\.


--
-- TOC entry 3452 (class 0 OID 25482)
-- Dependencies: 223
-- Data for Name: movimientos; Type: TABLE DATA; Schema: logitrack; Owner: postgres
--

COPY logitrack.movimientos (id, tipo, cantidad, fecha_hora, producto_id, bodega_origen_id, bodega_destino_id, usuario_id) FROM stdin;
1	ENTRADA	20	2026-07-21 09:20:20.376925	1	\N	1	\N
2	ENTRADA	15	2026-07-21 09:47:34.823887	1	\N	1	\N
3	TRANSFERENCIA	5	2026-07-23 13:40:53.240148	1	1	4	2
28	ENTRADA	50	2026-07-23 23:04:10.174133	1	\N	1	1
29	ENTRADA	20	2026-07-24 13:56:15.238981	1	\N	1	1
30	SALIDA	10	2026-07-24 13:56:54.848073	1	1	\N	1
31	TRANSFERENCIA	15	2026-07-24 13:57:37.860105	1	1	2	1
32	ENTRADA	4	2026-07-25 11:24:22.777537	4	1	2	\N
33	TRANSFERENCIA	7	2026-07-25 12:05:01.500318	5	2	6	\N
34	SALIDA	50	2026-07-25 13:15:38.347748	1	3	3	\N
35	TRANSFERENCIA	40	2026-07-25 17:35:43.352786	6	2	5	\N
\.


--
-- TOC entry 3450 (class 0 OID 25464)
-- Dependencies: 221
-- Data for Name: productos; Type: TABLE DATA; Schema: logitrack; Owner: postgres
--

COPY logitrack.productos (id, nombre, descripcion, precio, stock, bodega_id, categoria) FROM stdin;
5	Aceite Gourmet 1L	Botella de aceite vegetal 1000ml	18500.00	80	2	\N
6	Azúcar Incauca 1kg	Bolsa de azúcar refinada	3800.00	200	1	\N
4	Arroz Roa 1kg	Bolsa de arroz blanco 1kg	4500.00	154	1	\N
1	Laptop HP	Laptop HP Pavilion 15 Core i5 16GB	1500.00	95	1	\N
\.


--
-- TOC entry 3454 (class 0 OID 25505)
-- Dependencies: 225
-- Data for Name: usuarios; Type: TABLE DATA; Schema: logitrack; Owner: postgres
--

COPY logitrack.usuarios (id, password, rol, username) FROM stdin;
1	$2a$10$p5seBnIrTZqVWGIa.b8/QOzFv.Lbc9nTfi4vNJ8FxSMQSzW4hfmW6	ADMIN	admin1
2	$2a$10$p5seBnIrTZqVWGIa.b8/QOzFv.Lbc9nTfi4vNJ8FxSMQSzW4hfmW6	EMPLEADO	empleado1
\.


--
-- TOC entry 3465 (class 0 OID 0)
-- Dependencies: 226
-- Name: auditorias_id_seq; Type: SEQUENCE SET; Schema: logitrack; Owner: postgres
--

SELECT pg_catalog.setval('logitrack.auditorias_id_seq', 19, true);


--
-- TOC entry 3466 (class 0 OID 0)
-- Dependencies: 218
-- Name: bodega_id_seq; Type: SEQUENCE SET; Schema: logitrack; Owner: postgres
--

SELECT pg_catalog.setval('logitrack.bodega_id_seq', 6, true);


--
-- TOC entry 3467 (class 0 OID 0)
-- Dependencies: 222
-- Name: movimientos_id_seq; Type: SEQUENCE SET; Schema: logitrack; Owner: postgres
--

SELECT pg_catalog.setval('logitrack.movimientos_id_seq', 35, true);


--
-- TOC entry 3468 (class 0 OID 0)
-- Dependencies: 220
-- Name: productos_id_seq; Type: SEQUENCE SET; Schema: logitrack; Owner: postgres
--

SELECT pg_catalog.setval('logitrack.productos_id_seq', 6, true);


--
-- TOC entry 3469 (class 0 OID 0)
-- Dependencies: 224
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: logitrack; Owner: postgres
--

SELECT pg_catalog.setval('logitrack.usuarios_id_seq', 2, true);


--
-- TOC entry 3296 (class 2606 OID 25547)
-- Name: auditorias auditorias_pkey; Type: CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.auditorias
    ADD CONSTRAINT auditorias_pkey PRIMARY KEY (id);


--
-- TOC entry 3286 (class 2606 OID 25450)
-- Name: bodega bodega_pkey; Type: CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.bodega
    ADD CONSTRAINT bodega_pkey PRIMARY KEY (id);


--
-- TOC entry 3290 (class 2606 OID 25488)
-- Name: movimientos movimientos_pkey; Type: CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.movimientos
    ADD CONSTRAINT movimientos_pkey PRIMARY KEY (id);


--
-- TOC entry 3288 (class 2606 OID 25469)
-- Name: productos productos_pkey; Type: CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.productos
    ADD CONSTRAINT productos_pkey PRIMARY KEY (id);


--
-- TOC entry 3292 (class 2606 OID 25566)
-- Name: usuarios ukm2dvbwfge291euvmk6vkkocao; Type: CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.usuarios
    ADD CONSTRAINT ukm2dvbwfge291euvmk6vkkocao UNIQUE (username);


--
-- TOC entry 3294 (class 2606 OID 25512)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 3298 (class 2606 OID 25499)
-- Name: movimientos fk_movimiento_bodega_destino; Type: FK CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.movimientos
    ADD CONSTRAINT fk_movimiento_bodega_destino FOREIGN KEY (bodega_destino_id) REFERENCES logitrack.bodega(id);


--
-- TOC entry 3299 (class 2606 OID 25494)
-- Name: movimientos fk_movimiento_bodega_origen; Type: FK CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.movimientos
    ADD CONSTRAINT fk_movimiento_bodega_origen FOREIGN KEY (bodega_origen_id) REFERENCES logitrack.bodega(id);


--
-- TOC entry 3300 (class 2606 OID 25489)
-- Name: movimientos fk_movimiento_producto; Type: FK CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.movimientos
    ADD CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES logitrack.productos(id);


--
-- TOC entry 3301 (class 2606 OID 25521)
-- Name: movimientos fk_movimiento_usuario; Type: FK CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.movimientos
    ADD CONSTRAINT fk_movimiento_usuario FOREIGN KEY (usuario_id) REFERENCES logitrack.usuarios(id);


--
-- TOC entry 3297 (class 2606 OID 25470)
-- Name: productos fk_productos_bodega; Type: FK CONSTRAINT; Schema: logitrack; Owner: postgres
--

ALTER TABLE ONLY logitrack.productos
    ADD CONSTRAINT fk_productos_bodega FOREIGN KEY (bodega_id) REFERENCES logitrack.bodega(id) ON DELETE CASCADE;


-- Completed on 2026-07-25 17:45:09

--
-- PostgreSQL database dump complete
--

\unrestrict NJkVWZAvaUPhCjxnutVrf8sNfQLc7plLnlJuZ5dJyJJU9wUXUchua4kkHeVm1tf

-- Completed on 2026-07-25 17:45:09

--
-- PostgreSQL database cluster dump complete
--

