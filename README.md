# Sistemos paskirtis
Projekto tikslas – sukurti platformą, kuri supaprastintų darbo pasiūlymų skelbimo ir aplikavimo procesą bei užtikrintų patogų darbo pasiūlymų ir paraiškų valdymą vienoje vietoje. Sistema bus skirta tiek darbdaviams, tiek darbuotojams.

Darbdaviai galės skelbti ir valdyti darbo pasiūlymus, peržiūrėti kandidatų paraiškas, analizuoti jų gyvenimo aprašymus ir atrinkti tinkamiausius kandidatus.

Potencialūs darbuotojai turės galimybę peržiūrėti darbo pasiūlymus, kandidatuoti į juos, sekti savo aplikavimo istoriją bei įkelti gyvenimo aprašymą į sistemą, taip supaprastindami ir paspartindami paraiškos pateikimo procesą.
# Funckiniai reikalavimai
Neregistruotas sistemos naudotojas galės:

- Peržiūrėti darbo pasiūlymus;
- Registruotis.

Registruotas sistemos varotojas galės:

- Atsijungti;
- Peržiūrėti savo profilio informaciją;
- Redaguoti profilio informaciją;
- Įkelti naujausią CV versiją;
- Aplikuoti į darbo pasiūlymą;
- Peržiūrėti savo aplikacijas;
- Peržiūrėti aplikacijos detales;
- Atšaukti aplikaciją;
- Koreguoti aplikaciją;
- Prašyti profilio patobulinimo į darbdavio statusą.

Darbdavys galės:

- Sukurti skelbimą;
- Peržiūrėti savo sukurtus skelbimus;
- Koreguoti skelbimą;
- Šalinti skelbimą;
- Peržiūrėti skelbimo informaciją ir aplikantus;
- Patvirtinti ar atmesti aplikavimą.

Administratorius galės:

- Sukurti skelbimų kategoriją;
- Pašalinti kategoriją;
- Koreguoti kategoriją;
- Peržiūrėti visas sistemos kategorijas;
- Pašalinti kategoriją.

# Sistemos architektūra
Sistemos architektūra pateikiama žemiau (1 pav.) Diagramoje galima matyti, kad UI ir backend kodas yra debesyse naudojant Render Serverį, o Spring Boot aplikacija komunikuoja su PostgreSQL duomenų baze, kuri yra Supabase serveryje naudojant PostgreSQL protokolą.

![](https://github.com/user-attachments/assets/fef895c7-1604-49a2-aac1-3772fecc45cb)

1 pav. Sistemos architektūra

# Naudotojo sąsajos projektas
Žemiau yra pateikiama kuriamos sistemos dizaino wireframe‘ai (2 pav., 3 pav., 4 pav., 5 pav., 6 pav., 7 pav.)

![](https://github.com/user-attachments/assets/90d0ea78-10ce-4024-9298-f94864a5c989)

2 pav. Pagrindinio lango wireframe‘as 
<br> <br> <br>

![](https://github.com/user-attachments/assets/cc311500-1cc8-4404-812a-9872aa2ce769)

3 pav. Darbų peržiūros lango wireframe‘as
<br> <br> <br>

![](https://github.com/user-attachments/assets/130284ee-8564-44b1-9964-b778a690e9ae)

4 pav. Naudotojo aplikacijų lango wireframe‘as
<br> <br> <br>

![](https://github.com/user-attachments/assets/8273c174-bf0e-4b2b-a323-12a5d2bee926)

5 pav. Profilio peržiūros lango wireframe‘as
<br> <br> <br>

![](https://github.com/user-attachments/assets/286ecb37-515b-4078-89e5-65ae55742c3b)

6 pav. Darbdavio sukurtų darbų peržiūros lango wireframe‘as
<br> <br> <br>

![](https://github.com/user-attachments/assets/a14c691a-f413-4923-90d5-cc63b6ab98ea)

7 pav. Kategorijų esančių sistemoje peržiūros lango wireframe‘as
<br> <br> <br>


Šie wireframe‘ai buvo naudojami kuomet buvo kuriama pagrindinė aplikacija.

Pagrindinio puslapio implementacija yra pateikiama žemiau (8 pav.), o wireframe‘as pateikiamas aukščiau (2 pav.)

![](https://github.com/user-attachments/assets/be6ee5e2-1a1a-4f2a-b471-1ce85f9b6114)

8 pav. Pagrindinis langas
<br> <br> <br>


Darbų peržiūros lango implementacija pateikiama žemiau (9 pav.), o wireframe‘as pateikiamas aukščiau (3 pav.)

![](https://github.com/user-attachments/assets/b340d64c-c13b-45bd-8686-38b2d23e5151)

9 pav. Darbų peržiūros langas
<br> <br> <br>


Naudotojo aplikacijų peržiūrėjimo langas pateikiamas žemiau (10 pav.), o jo wireframe‘as pateikiamas aukščiau (4 pav.)

![](https://github.com/user-attachments/assets/05561beb-1551-438b-a50b-6b1548de047a)

10 pav. Naudotojo aplikacijų langas
<br> <br> <br>


Profilio peržiūros langas yra pateikiamas žemiau (11 pav.), o wireframe‘as pateikiamas aukščiau (5 pav.)

![](https://github.com/user-attachments/assets/0092c009-0e8a-418d-9977-9665119764c4)

11 pav. Profilio peržiūros langas
<br> <br> <br>


Darbadavio sukurtų darbų peržiūros langas pateikiamas žemiau (12 pav.), o wireframe‘as pateikiamas aukščiau (6 pav.)

![](https://github.com/user-attachments/assets/4622d48c-c54e-4a9b-8c5f-3d1f5362d9c9)

12 pav. Darbdavio sukurtų darbų peržiūros langas
<br> <br> <br>


Administratoriaus naudojamas puslapis, kurio viduje galime peržiūrėti bei valdyti sistemoje esančias kategorijas pateikiamas žemiau (13 pav.), o wireframe‘as pateikiamas aukščiau (7 pav.)

![](https://github.com/user-attachments/assets/dd2cdd1d-a595-4ef0-847f-4472a75ef19d)

13 pav. Kategorijų esančių sistemoje peržiūros langas

# API specifikacija

[API specifikacija](/src/main/resources/openapi/jobly_workflow_openapi.yaml)

# Išvados
Atlikto darbo metu buvo įgyta patirtis naudojant Spring Boot backend kūrimui ir React frontend kūrimui, taip pat suprasta šių technologijų tarpusavio integracija.
Buvo taikoma Swagger dokumentacija klasėms generuoti, kas supaprastino API kūrimo ir naudojimo procesą.
Įgyvendintas projektas buvo sėkmingai diegiamas į Render platformą, įgyvendinant diegimo procesus bei aplinkos konfigūravimą.
Įgyta patirtis prisidėjo prie supratimo apie modernių web aplikacijų kūrimo, valdymo ir dokumentacijos praktikas.