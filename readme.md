# IDH15 Integratie project


## Installeren en starten (PHP)

1. Clone het project
2. `$ cd frontend`
3. `$ composer install` (installeer dependencies)
4. Maak een DB aan + tabel a.d.h.v. `db.sql`
5. Kopieer `.env.example` naar `.env` en vul de juiste waardes in
6. `$ php -S localhost:8000 -t web/` (start lokale webserver)

## Installeren en starten (Java)

1. `$ cd apache-camel\Integratie`
2. Kopieer `example.properties` naar `config.properties` en vul de juiste waardes in
2. `$ mvn compile exec:java -Dexec.mainClass=org.groepc.App`

## Continuous Integration/Deployment

[ ![Codeship Status for groepc/IDH15-integratie](https://app.codeship.com/projects/e4c2a890-e181-0134-5caa-76fc8c1e432b/status?branch=master)](https://app.codeship.com/projects/205535)