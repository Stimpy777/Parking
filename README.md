# Parking

## Anforderungen:  
„Hausaufgabe“ - Intelligente Parkplatzsuche
Hintergrund:
Stell dir vor, es ist ein System für ein großes Parkhaus mit mehreren Etagen und Stellplätzen zu entwickeln.
Das System soll Autofahrern helfen, schnell einen oder mehrere freie Parkplätze zu finden (Parkleitsystem).
Eine einfache "erster freier Platz"-Logik ist nicht ausreichend. Stattdessen soll das System versuchen,
Parkplätze intelligenter zu vergeben, um die Auslastung zu optimieren und die Suchzeiten zu minimieren.
Anforderungen:
1. Modellierung: Entwerfe eine geeignete Datenstruktur in Java, um das Parkhaus mit seinen
   Etagen, Stellplätzen, Belegungszustand, Entfernung zum nächsten Ausgang etc. zu repräsentieren.
   Berücksichtige, dass das Parkhaus eine variable Anzahl von Etagen und Stellplätzen pro Etage haben
   kann. Eine Persistierung der Daten ist nicht notwendig.
2. Parkplatzsuche (Strategie): Implementiere eine Strategie zur Parkplatzsuche, die folgende
   Kriterien berücksichtigt (Priorität absteigend):
   •Gruppierung: Versuche, wenn möglich, zusammenhängende freie Parkplätze zu finden (z.B.
   für Familien, die nebeneinander parken möchten).
   •Nähe zum Ein-/Ausgang: Bevorzuge freie Parkplätze in der Nähe eines
   Etagen-Ein-/Ausgangs.
   •Gleichmäßige Auslastung: Vermeide es, einzelne Bereiche des Parkhauses übermäßig zu
   füllen, während andere Bereiche leer bleiben.
3. Parken und Verlassen: Implementiere ein Java-API, dass dein Datenmodell und die
   Suchstrategie nutzt und Funktionen anbietet, um ein oder mehrere Fahrzeuge zu „parken“ (Status
   auf "belegt" setzen) und um ein Fahrzeug von einem Parkplatz zu „entfernen“ (Status auf "frei"
   setzen).
4. Tests: Erstelle für dein API sinnvolle Unit-Tests. Eine vollständige Code-Abdeckung ist nicht
   erforderlich.
   Was abzugeben / mitzubringen ist:
   • Der Java-Quellcode
   • Gerne als Github- oder Gitlab-Projekt, du kannst aber auch dein Notebook mitbringen
   Terminvorbereitung:
   In unserem nächsten Termin möchten wir gern über deinen Lösungsansatz sprechen. Bereite dich darauf vor,
   die Datenstruktur, deine Implementierungsstrategie und Designentscheidungen zu besprechen.
---
## Lösung:
Die Klasse Parkhausservice bietet eine API mit der ein solches Parkhaus erzeugt werden kann. 
Der Service bietet Methoden zum Parken von Fahrzeugen und zum Verlassen des Parkhauses. Zusätzlich
gibt es Methoden zur Anzeige der Anzahl der freien sowie belegten Parkplätzen. 

Zum Testen der Anwendung gibt es eine Main-Klasse mit einer main()-Methode, die beispielhaft die
Verwendung und Funktionsweise der API veranschaulicht.  
---
## Dokumentation:

Umgebung:  
- Java 21
- Apache-Maven 3.

Kompilierung:  
`mvn clean compile`

Starten der Tests:  
`mvn clean verify`

Paketieren:  
`mvn clean package`

Starten der Anwendung:  
`java -jar .\target\parking-1.0.0.jar`
