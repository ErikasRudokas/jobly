## 1. Sprendžiamo uždavinio aprašymas

### 1.1. Sistemos paskirtis
Projekto tikslas – sukurti platformą, kuri supaprastintų darbo pasiūlymų skelbimo ir aplikavimo procesą bei užtikrintų patogų darbo pasiūlymų ir paraiškų valdymą vienoje vietoje. Sistema bus skirta tiek darbdaviams, tiek darbuotojams.  
Darbdaviai galės skelbti ir valdyti darbo pasiūlymus, peržiūrėti kandidatų paraiškas, analizuoti jų gyvenimo aprašymus ir atrinkti tinkamiausius kandidatus.  
Potencialūs darbuotojai turės galimybę peržiūrėti darbo pasiūlymus, kandidatuoti į juos, sekti savo aplikavimo istoriją bei įkelti gyvenimo aprašymą į sistemą, taip supaprastindami ir paspartindami paraiškos pateikimo procesą.

### 1.2. Funkciniai reikalavimai
**Neregistruotas sistemos naudotojas galės:**
1. Peržiūrėti darbo pasiūlymus;
2. Registruotis.

**Registruotas sistemos varotojas galės:**
1. Atsijungti;
2. Peržiūrėti savo profilio informaciją;
3. Redaguoti profilio informaciją;
4. Įkelti naujausią CV versiją;
5. Aplikuoti į darbo pasiūlymą;
6. Peržiūrėti savo aplikacijas;
7. Peržiūrėti aplikacijos detales;
8. Atšaukti aplikaciją;
9. Koreguoti aplikaciją;
10. Prašyti profilio patobulinimo į darbdavio statusą.

**Darbdavys galės:**
1. Sukurti skelbimą;
2. Peržiūrėti savo sukurtus skelbimus;
3. Koreguoti skelbimą;
4. Šalinti skelbimą;
5. Peržiūrėti skelbimo informaciją ir aplikantus;
6. Patvirtinti ar atmesti aplikavimą.

**Administratorius galės:**
1. Sukurti skelbimų kategoriją;
2. Pašalinti kategoriją;
3. Koreguoti kategoriją;
4. Peržiūrėti visas sistemos kategorijas;
5. Peržiūrėti kategoriją.

---

## 2. Pasirinktos technologijos
Sistema bus sudaryta iš dviejų dalių: „backend“ ir „frontend“.  
„Backend“ sistema bus rašoma naudojant **Spring Boot**, bei komunikuos su **PostgreSQL** duomenų baze, kurioje bus saugoma aplikacijai reiklainga informacija.

„Frontend“ sistema bus rašoma naudojant **React.js** kartu su **Typescript**. Siekiant praleisti mažiau laiko kuriant dizainus bus taip pat naudojama **MUI (Material-UI)**, ši bibliotekal eidžia naudoti jau aprašytus komponentus.

Autorizacijai bus naudojama **JWT tokenai**. Šie token‘ai bus generuojami „backend“ aplikacijos, o „frontend“ atliekant užklausas turės pateikti šį token‘ą kiekvienos užklausos metos, kuri yra skirta autentifikuotiems naudotojams. Siekiant didesnio saugumo naudojant JWT token‘us bus taip pat naudojama **refresh token‘ai**. Tai leis sumažinti JWT token‘ų galiojimo laika siekiant minimizuoti saugumo spragas.