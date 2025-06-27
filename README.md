# Návrh algoritmu na tvorbu turnusov v regionálnej autobusovej doprave pri nedostatku vodičov

## Popis Projektu

Tento projekt predstavuje inovatívne riešenie pre jednu z najväčších výziev súčasnej regionálnej autobusovej dopravy – **chronický nedostatok kvalifikovaných vodičov**. Diplomová práca sa ponára do problematiky s cieľom navrhnúť a implementovať robustný algoritmus, ktorý optimalizuje tvorbu vodičských turnusov. Naším primárnym cieľom je **minimalizovať negatívny dopad na cestujúcu verejnosť** a zabezpečiť kontinuitu dopravných služieb, a to všetko pri striktnom dodržiavaní komplexnej legislatívy týkajúcej sa pracovného času a odpočinku vodičov. Projekt sa snaží transformovať výzvy plynúce z obmedzených ľudských zdrojov na príležitosť pre efektívnejšie a udržateľnejšie fungovanie dopravy.

## Navrhované Riešenie

V srdci nášho riešenia leží **pokročilý matematický model a sofistikovaný algoritmus**, navrhnutý na dynamický výpočet optimalizovaných turnusov. Tento systém je schopný riešiť kritickú úlohu výberu spojov, ktoré majú byť obslúžené aj v podmienkach nedostatku vodičov, s precíznym zohľadnením všetkých legislatívnych noriem. Medzi ne patria nielen minimálne doby odpočinku, ale aj časy jazdy, bezpečnostné prestávky a predpísané prestávky na odpočinok a jedenie, čím je zabezpečená vysoká úroveň súladu a bezpečnosti.

Náš optimalizačný rámec je navrhnutý tak, aby flexibilne reagoval na rôzne prevádzkové priority, zahŕňajúc kritériá ako:
* **Minimalizácia počtu potrebných autobusov:** Efektívne využitie flotily vozidiel.
* **Minimalizácia počtu vodičov:** Optimalizácia nasadenia dostupného personálu.
* **Maximalizácia obsadenosti spojov všetkých liniek:** Zvýšenie kapacity a spokojnosti cestujúcich.
* **Maximalizácia počtu obslúžených spojov:** Zabezpečenie širokej dostupnosti služieb.
* **Minimalizácia počtu neobslúžených cestujúcich:** Zníženie negatívnych dopadov na verejnosť.

Výsledkom je integrovaný softvérový nástroj, ktorý predstavuje most medzi teoretickými matematickými modelmi a praktickou aplikáciou. Tento nástroj umožňuje dôkladné testovanie optimalizačných modelov na **reálnych vstupných údajoch z regionálnej autobusovej dopravy**, čím sa zaisťuje jeho relevancia a spoľahlivosť v praxi.

## Kľúčové Vlastnosti

* **Inteligentná Optimalizácia Turnusov**: Algoritmus navrhnutý pre dynamické a efektívne generovanie vodičských turnusov, ktoré maximalizujú využitie zdrojov a minimalizujú prestoje.
* **Komplexné Rešpektovanie Legislatívy**: Systém automaticky zohľadňuje a zabezpečuje dodržiavanie všetkých slovenských a európskych legislatívnych noriem týkajúcich sa pracovného času, prestávok a odpočinku vodičov, čím eliminuje riziko porušenia predpisov.
* **Strategické Riešenie Nedostatku Vodičov**: Ponúka pokročilé modely pre strategické rozhodovanie o prioritizácii a obsluhe spojov v situáciách s obmedzeným počtom vodičov, s cieľom minimalizovať negatívny dopad na cestujúcich.
* **Intuitívne Grafické Zobrazenie Turnusov**: Umožňuje používateľom vizuálne analyzovať vytvorené turnusy, čo vedie k lepšiemu pochopeniu pridelených úloh a umožňuje rýchlu identifikáciu potenciálnych zlepšení.
* **Prehľadné Štatistické Grafy**: Prezentuje kľúčové štatistiky a výsledky optimalizácie v ľahko čitateľných grafických formátoch, poskytujúc cenné informácie pre manažment a plánovanie.
* **Robustné Overenie na Reálnych Dátach**: Všetky modely boli experimentálne overené pomocou rozsiahlych súborov reálnych dát z regionálnej autobusovej dopravy, čo zaručuje praktickú aplikovateľnosť a vysokú presnosť riešenia.

## Inštalácia a Používanie

Tento projekt je spravovaný pomocou Maven, čo uľahčuje správu závislostí a proces zostavovania.

### Predpoklady

Pred spustením projektu sa uistite, že máte nainštalované nasledujúce:

* **Java Development Kit (JDK)**: Verzia 8 alebo vyššia.
* **Apache Maven**: Pre správu projektu a zostavovanie.
* **Gurobi Optimizer**: Pre riešenie optimalizačných modelov. Uistite sa, že máte správne nastavenú licenciu a systémové premenné pre Gurobi.

### Klonovanie Repozitára

Naklonujte si repozitár do lokálneho adresára pomocou nasledujúceho príkazu:

```bash
git clone [https://github.com/Jakub969/ISDP.git](https://github.com/Jakub969/ISDP.git)
cd ISDP
```
V rámci IDE (IntelliJ IDEA, Eclipse atď.):
Projekt môžete tiež importovať ako Maven projekt do vášho obľúbeného IDE a spustiť ho priamo z prostredia IDE.

### Používanie Aplikácie
Po spustení aplikácie budete môcť:

* **Načítať vstupné dáta**: Obvykle zo súborov obsahujúcich informácie o linkách, spojoch, časoch a dostupnosti vodičov.
* **Nastaviť optimalizačné parametre**: Vybrať si z dostupných optimalizačných kritérií (napr. minimalizácia vodičov, maximalizácia obslúžených spojov).
* **Spustiť výpočet turnusov**: Aplikácia použije Gurobi Optimizer na nájdenie optimálneho riešenia.
* **Analyzovať výsledky**: Prezerať si generované turnusy prostredníctvom **grafického zobrazenia** a analyzovať kľúčové štatistiky pomocou **štatistických grafov**. Tieto vizualizácie vám pomôžu rýchlo pochopiť a vyhodnotiť efektivitu navrhnutých riešení.

Pre detailnejšie informácie o funkčnosti a pokročilých nastaveniach sa obráťte na dokumentáciu projektu.


