# HackHub – UML & Documentazione

Progetto di Ingegneria del Software: piattaforma di gestione hackathon **HackHub**.
Questo repository contiene l'analisi, il progetto, i diagrammi UML (PlantUML) e il codice sorgente Java.

## Struttura

```
.
├── 00-knowledge-base/      Tracce e materiale di riferimento (design patterns, ecc.)
├── 01-analisi/             Fase di analisi
│   ├── 01-casi-uso/        Diagramma e schede dei casi d'uso
│   ├── 02-classi-analisi/  Diagramma delle classi di analisi
│   └── 03-sequenze/        Diagrammi di sequenza per UC
├── 02-progetto/            Diagramma delle classi di progetto
├── 03-codice/              Codice Java (modello, exceptions, observer, strategy, ...)
├── 04-export-vp/           Esportazioni Visual Paradigm
├── docs/                   Slide del corso e materiale didattico
└── ITERAZIONI.md           Log delle iterazioni di sviluppo
```

## Visualizzare i diagrammi PlantUML

I file `.puml` presenti nel repository possono essere visualizzati online tramite il sito ufficiale di PlantUML:

**https://www.plantuml.com/**

In particolare puoi usare il **PlantUML Online Server** (https://www.plantuml.com/plantuml) per incollare il contenuto di un `.puml` e renderizzarlo come immagine.

In alternativa, molti IDE (IntelliJ IDEA, VS Code) supportano l'anteprima nativa tramite estensioni PlantUML.

## Codice

Il codice Java si trova in `03-codice/src/` (sorgenti) e `03-codice/test/` (test).
Le librerie sono in `03-codice/lib/`. La build (`out/`) è esclusa da git.
