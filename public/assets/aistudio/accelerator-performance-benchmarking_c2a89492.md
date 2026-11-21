# KI-Beschleunigerleistung und ‑benchmarking &nbsp;|&nbsp; AI and ML &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/ai-ml/accelerator-performance-benchmarking

Die Bewertung von KI-Hardware für die Verwendung mit Large Language Models (LLMs) als primäre Arbeitslast erfordert einen konsistenten, anbieterneutralen Ansatz. In diesem Leitfaden wird eine Methode zum Vergleichen der Leistung von KI-Beschleunigerchips verschiedener Anbieter wie NVIDIA, AMD, Googleund AWS beschrieben. Die Grundsätze und die Methodik lassen sich an jeden KI-Chip oder jede Arbeitslast anpassen. Die Beispiele konzentrieren sich jedoch auf eine gängige Kombination aus NVIDIA-Grafikprozessoren (GPUs) und GoogleTensor Processing Units (TPUs), auf denen LLM-Arbeitslasten ausgeführt werden.

Modelle werden in der Regel für eine bestimmte Hardwareplattform optimiert. Daher reicht es nicht aus, nur die Modellleistung zu bewerten, um die Fähigkeiten der Hardware zu verstehen. Bei der Bewertung von Beschleunigerchips für LLMs sollten Sie drei wichtige Aspekte berücksichtigen: Microbenchmarking, Roofline-Analyse und Modell-Benchmarking für Training und Inferenz.

Microbenchmarking und Roofline-Analysen sind unerlässlich, um die Fähigkeiten und das Potenzial einer bestimmten Beschleunigerplattform zu verstehen. Sobald diese Informationen bekannt sind, können Sie die Modelle für Training und Inferenz vergleichen und so die tatsächlichen Arbeitslasten auf verschiedenen Chips vergleichen. Außerdem erhalten Sie Einblicke, ob die Modellarchitektur für eine bestimmte Plattform optimiert ist.

Leistungsdimensionen
--------------------

Wir empfehlen, die Leistung eines bestimmten Beschleunigersystems anhand von drei Dimensionen zu bewerten, um ein umfassenderes Bild zu erhalten:

*   **Microbenchmarking**:Die höchsten Hardwarespezifikationen bedeuten nicht, dass Anwendungen diese Spezifikationen tatsächlich nutzen können. Mit Microbenchmarking können Sie bewerten, wie sich Gleitkommaoperationen pro Sekunde (FLOPS), High Bandwidth Memory (HBM) und Netzwerkbandbreite auf die Leistung bei realen Arbeitslasten auswirken.
*   **Roofline-Analyse**:Die optimale Hardwareauslastung kann durch die Speicherbandbreite oder die Rechengeschwindigkeit beeinträchtigt werden. Mit einem Roofline-Modell und der Operational Intensity (OI) verschiedener Systemkomponenten können Sie sehen, wie gut Hardware und Arbeitslasten zueinander passen. Eine Kombination aus Microbenchmarks und Rooflines bietet eine theoretische Bewertung dessen, was ausgewählte Hardware für verschiedene Arten von Arbeitslasten leisten kann.
*   **Modell-Benchmarking**:Durch Benchmarking für Trainings- und Inferenz-Arbeitslasten, um die Anzahl der Tokens pro Sekunde pro Chip (TPS/Chip) zu messen, können Sie dasselbe Modell auf verschiedenen Plattformen bewerten. Wenn die ersten Ergebnisse von der Mikro-Benchmarking- und Roofline-Analyse abweichen, ist das ein Hinweis darauf, dass zusätzliche Softwarearbeiten erforderlich sind, um die zuvor ermittelten Rooflines zu erreichen. Dazu kann es beispielsweise erforderlich sein, Sharding-Strategien zu ändern oder benutzerdefinierte Kernel zu verwenden.

Denken Sie daran, dass das Benchmarking von Modellen eine Momentaufnahme für ein bestimmtes Modell, eine bestimmte Skalierung und eine bestimmte Plattform ist. Erfahrene Nutzer berücksichtigen bei der Bewertung der Leistung auch Branchentrends (z. B. Modellarchitektur), Microbenchmarking und Roofline-Ergebnisse.

### Gemeinsames Design von Modell und Hardware

Bei Leistungsbewertungen muss die Modellarchitektur im Kontext der getesteten Hardware sorgfältig berücksichtigt werden. Effizient entwickelte Modelle werden oft für eine bestimmte Hardwareplattform entwickelt, um die spezifischen Nuancen der Plattform zu nutzen. Daher werden andere Plattformen oder sogar verschiedene Generationen derselben Plattform möglicherweise nicht vollständig genutzt. Ein für NVIDIA Hopper-GPUs entwickeltes Modell nutzt AMD- oder NVIDIA Blackwell-GPUs möglicherweise nicht vollständig aus.

Diese Überlegung ist besonders wichtig, wenn Sie Hardwareplattformen mit unterschiedlichen Funktionen verwenden, da ein für eine Plattform entwickeltes Modell möglicherweise Konfigurations- oder Softwareänderungen oder beides erfordert, um auf einer anderen Plattform die maximale Leistung zu erzielen. Das Benchmarking optimierter Modelle ist unerlässlich, um die Marketingaussagen von Anbietern zur „theoretischen Spitzenleistung“ zu validieren und die Ergebnisse in der Praxis zu messen. Das unabhängige Analystenunternehmen SemiAnalysis [merkt an](https://newsletter.semianalysis.com/i/180102610/why-anthropic-is-betting-on-tpus): „Der Vergleich der theoretischen FLOPS ist nur ein Teil der Geschichte. Wichtig sind die effektiven FLOPS, da Spitzenwerte in der Praxis fast nie erreicht werden.“

#### Beispiel: die `gpt-oss-120B`\-Challenge

Ein häufiges Problem beim Benchmarking ist die Bewertung eines Modells auf Hardware, für die es nicht entwickelt wurde. Das `gpt-oss-120B` [Open-Weights-Modell](https://huggingface.co/openai/gpt-oss-120b) von OpenAI ist ein Beispiel dafür, warum die Modellarchitektur eng an das Ziel-Silizium angepasst werden muss. Das folgende Beispiel zeigt, dass die gemeinsame Entwicklung von Modellen entscheidend ist und früh im Prozess erfolgen muss.

Das `gpt-oss-120B`\-Modell verwendet eine Dimension von 64 für den Attention-Head. Obwohl dies für viele GPU-optimierte Modelle Standard ist, führt es zu einer architektonischen Diskrepanz für TPU-Beschleuniger. TPUs wie [Trillium](https://docs.cloud.google.com/tpu/docs/v6e?hl=de) und [Ironwood](https://docs.cloud.google.com/tpu/docs/tpu7x?hl=de) sind für Matrixdimensionen optimiert, die Vielfache von 256 sind, um ihre Matrixmultiplikationseinheiten (MXUs) vollständig zu sättigen. Da die Head-Dimension 64 nicht für TPUs optimiert ist, führt die Ausführung von `gpt-oss-120B` auf einem TPU-System zu einer geringeren Anzahl von Tokens pro Sekunde (TPS) und einer geringeren FLOP-Auslastung des Modells (MFU). Die Hardware verschwendet effektiv Taktzyklen und Strom, indem sie den verbleibenden Speicherplatz mit Nullen auffüllt, um die 256 × 256-Ausführungsraster zu füllen.

Wenn `gpt-oss-120B` als Benchmark für TPUs verwendet wird, kann dies fälschlicherweise auf eine schlechte Hardwareleistung hinweisen, obwohl es in Wirklichkeit auf eine Diskrepanz zwischen Softwarearchitektur und Hardware zurückzuführen ist. Um die Obergrenze eines Beschleunigers genau zu bestimmen, testen Sie ihn mit Modellen, die speziell für seine Geometrie entwickelt wurden. Beispiele sind Modelle mit 128 oder 256 Head-Dimensionen wie Gemma 4. Sie können die Leistung dieses Modells mit benutzerdefinierten Kernels verbessern, die das Auffüllen mit Nullen vermeiden und stattdessen die MXU „auffüllen“. Dies erfordert jedoch Fachwissen und erreicht nicht dasselbe Leistungsniveau wie auf GPUs. Sie können auch die Head-Dimensionen ändern, um sie besser für TPUs zu optimieren. Dadurch werden jedoch die vorhandenen Modellgewichte ungültig und das Modell muss neu trainiert werden.

Grundsätze für Benchmarking
---------------------------

Damit die Benchmarking-Ergebnisse über verschiedene Beschleuniger hinweg fair und zukunftssicher sind, sollten Sie die folgenden Grundsätze berücksichtigen:

*   **Leistung pro Dollar im Fokus**:Einige Anbieter konzentrieren sich auf die Rohleistung einzelner Chips. Die Leistung pro Dollar auf Systemebene ist jedoch repräsentativer für die Gesamtbetriebskosten und den Wert. Wenn Chip A 20% leistungsfähiger und 50% teurer als Chip B ist, sollten die Tester die Vorteile von Chip B in Bezug auf die Leistung pro Dollar berücksichtigen. Berücksichtigen Sie auch die Leistung pro Watt als Teil der Kosten.
*   **Moderne KI-Arbeitslasten darstellen**:Konzentrieren Sie sich auf beliebte Transformer-basierte Modelle, große Cluster und die neuesten Frameworks und berücksichtigen Sie dabei Branchentrends. Die Umstellung der Branche auf spärlichere [MoE](https://wikipedia.org/wiki/Mixture_of_experts)\-Modelle (Mixture of Experts) erschwert beispielsweise die vollständige Optimierung von FLOPS und erfordert eine höhere Bisektionsbandbreite von Netzwerken.
*   **Breite Unterstützung für Entwickleranforderungen sicherstellen**:Berücksichtigen Sie die Leistung, Flexibilität und Skalierbarkeit für verschiedene Arbeitslasten: Training, Optimierung und Bereitstellung für eine Reihe von LLMs und anderen Modellen.
*   **Anbieterunabhängige Modelle und Tools auswählen**:Wählen Sie Modelle und Engines aus, die auf verschiedenen Beschleunigern ausgeführt werden können, um die Auswertung über mehrere Beschleuniger hinweg zu vereinfachen. Verwenden Sie beispielsweise offene Modelle wie Qwen und Gemma sowie Open-Source-Inferenz-Engines, die auf GPUs und TPUs ausgeführt werden, z. B. vLLM. Vermeiden Sie hardwarespezifische PyTorch-/CUDA-Stacks. Für das Benchmarking des Modelltrainings sind anbieterspezifische Frameworks (wie [MaxText](https://maxtext.readthedocs.io/en/latest/index.html) für TPUs und Megatron für GPUs) am nützlichsten, wenn die Modelle plattformübergreifend konstant gehalten werden.
*   **Gemeinsames Design von Modellen**:Erfahrene Nutzer entwickeln ihre Modelle gemeinsam, um die Hardwareplattform optimal zu nutzen. Ein auf Chip A trainiertes Modell wird auf Chip B nicht automatisch eine gute Leistung erbringen.
*   **Gesamtes Hardwaresystem berücksichtigen**:Einige Beschleuniger können in einem Bereich wie FLOPS eine hohe Leistung aufweisen. Engpässe in anderen Bereichen, z. B. bei der Speicherbandbreite, können die Fähigkeiten des Beschleunigers jedoch erheblich einschränken. Weitere Aspekte des Systems, die berücksichtigt werden müssen, sind die Chipspezifikationen, die Chipvernetzung und die Scale-out-Architektur.
*   **Zuverlässigkeit von Hardware und Software**:Unterbrechungen bei umfangreichen Trainings- oder wichtigen Inferenzvorgängen können sehr kostspielig sein. Ebenso ist ein KI-Beschleuniger nur so nützlich wie die Software, die darauf ausgeführt wird. Ein ausgereifter und zuverlässiger Software-Stack, der sich in großem Maßstab bewährt hat, ist unerlässlich, um den Wert zu maximieren.

Microbenchmarks
---------------

Beim Benchmarking von Beschleunigern werden beim Microbenchmarking bestimmte Hardwarekomponenten wie Rechenkerne, Arbeitsspeicher und Verbindungen isoliert, um ihre absoluten Grenzen ohne die Beeinträchtigung durch komplexe Softwarestacks zu messen. Viele Anbieter heben „Peak-FLOPS auf einem einzelnen Chip“ hervor, aber in der realen Welt ist KI ein Problem verteilter Systeme. Mit Microbenchmarking können Sie herausfinden, ob ein Chip nur für sich genommen leistungsstark ist oder für den Einsatz in Rechenzentren konzipiert wurde.

Mit Microbenchmarking können Sie die Spitzenleistung der Hardware messen und die realen Grenzen des Systems unabhängig von der Modellarchitektur ermitteln. Microbenchmarking ist besonders nützlich, wenn Sie Beschleuniger für zukünftige oder unbestimmte Anwendungsfälle und Modellarchitekturen bewerten.

Um Beschleuniger effektiv zu mikrobenchmarken, sollten Sie Folgendes bewerten:

| Benchmark | Erklärung |
| --- | --- |
| Auslastung der dichten allgemeinen Matrixmultiplikation (GEMM) | Führen Sie hochoptimierte GEMM-Kernel mit verschiedenen Genauigkeiten aus, um die rohe, nachhaltige mathematische Leistung der wichtigsten Recheneinheiten des Beschleunigers zu messen. |
| Streaming von High Bandwidth Memory (HBM) | Führen Sie Microbenchmarks für die Arbeitsspeicherbandbreite aus, um die anhaltenden Lese-, Schreib- und Kopiergeschwindigkeiten des Onboard-Arbeitsspeichers des Beschleunigers zu messen. Architekturen, die ein gesundes Verhältnis von Byte zu FLOP aufrechterhalten, verhindern, dass die Rechenkerne im Leerlauf sind. |
| Verteilte Kollektive (All-Reduce und All-Gather) | Führen Sie standardisierte Tests für die kollektive Kommunikation auf Tausenden von Chips aus, um zu messen, wie stark die Netzwerkbandbreite und ‑latenz mit zunehmender Clustergröße abnehmen. |
| Übertragungsraten von Host zu Gerät (H2D) und von Gerät zu Host (D2H) | Übertragen Sie große, kontinuierliche Datenstreams zwischen dem Systemspeicher der Host-CPU und dem Beschleuniger, um die Übertragungsraten über den PCIe-Bus oder die benutzerdefinierte Verbindung zu messen. |
| Anhaltendes Drosseln der Leistung aufgrund von Überhitzung und Stromverbrauch | Führen Sie einen GEMM-Loop mit maximaler Auslastung 48 Stunden lang aus und überwachen Sie dabei die Leistungsaufnahme auf Rackebene, um die thermische Stabilität und die Energieeffizienz in der Praxis zu bewerten. |

### Beispiel für einen Microbenchmark-Vergleich

Hier ist ein **beispielhafter** Vergleich zwischen zwei Chips, bei dem ein hypothetischer Chip A besser als ein hypothetischer Chip B erscheint, in der Praxis aber schlechter abschneidet:

| Benchmark name | Ergebnis des Tests von Chip A | Chip A – Spezifikation | Verhältnis von Tests zu Spezifikationen | Ergebnis des Tests von Chip B | Chip B-Spezifikation | Verhältnis von Tests zu Spezifikationen |
| --- | --- | --- | --- | --- | --- | --- |
| Chip-zu-Chip-Netzwerk | 800 GBps | 1.000 GBps | 80,0% | 850 GBps | 900 GBps | **94,4%** |
| gemm/peakTOPS | 1.800 TFLOPS | 2.500 TFLOPS | 72,0% | 1.800 TFLOPS | 2.000 TFLOPS | **90,0%** |
| Arbeitsspeicherbandbreite | 6.000 GBps | 8.000 GBps | 75,0% | 6.500 GBps | 7.500 GBps | **86,7%** |
| Host-zu-Gerät | 58 GB/s pro Chip | 70 GB/s pro Chip | 82,9% | 60 GB/s pro Chip | 65 GB/s pro Chip | **92,3%** |
| Gerät zu Host | 55 GB/s pro Chip | 70 GB/s pro Chip | 78,6% | 55 GB/s pro Chip | 65 GB/s pro Chip | **84,6%** |

Analyse der Dachlinie
---------------------

Eine [Roofline-Analyse](https://wikipedia.org/wiki/Roofline_model) (oder ein Roofline-Modell) kann Ihnen eine Visualisierung zur Analyse der Operational Intensity (OI) verschiedener Systemkomponenten und zur Bewertung der Eignung bestimmter Designs für bestimmte Plattformen liefern.

Der Durchsatz eines KI-Beschleunigerchips wird durch drei Hauptfaktoren begrenzt:

1.  **Rechenkapazität**:Der maximale mathematische Durchsatz des Chips (FLOPS).
2.  **Speicherbandbreite**:Die Geschwindigkeit, mit der Daten zum oder vom lokalen High Bandwidth Memory (HBM) des Chips übertragen werden können.
3.  **Netzwerkbandbreite**:Die Geschwindigkeit, mit der Daten während des verteilten Trainings oder der Inferenz über das Chip-Netzwerk zwischen mehreren Chips ausgetauscht werden können. Beispiel: die Übertragungsrate von ICI (für TPUs) oder NVLink (für GPUs).

Weitere Informationen zu Dachlinien finden Sie unter [All About Rooflines](https://jax-ml.github.io/scaling-book/roofline/).

Das Standard-Roofline-Diagramm besteht aus zwei Achsen:

*   **X-Achse (operative Intensität)**: Die operative Intensität ist das Verhältnis von Rechenaufwand (FLOPS) zu Speicherverkehr (übertragene Byte), ausgedrückt als FLOPS pro Byte. Sie gibt an, wie viel Rechenarbeit für jedes Byte an Daten erforderlich ist, das aus dem Arbeitsspeicher abgerufen wird.
*   **Y-Achse (erreichbare Leistung)**: Die erreichbare Leistung wird in FLOPS pro Sekunde ausgedrückt. Er gibt den tatsächlich erreichten Rechen-Durchsatz an.

Das „Dach“ wird durch zwei sich schneidende Linien gebildet, die die Hardware-Maximalwerte darstellen:

1.  **Das schräge Dach (speichergebunden)**: Erreichbare Leistung = Spitzenwert der Speicherbandbreite × Betriebliche Intensität. Auf dieser Linie wird die Leistung streng dadurch begrenzt, wie schnell Daten an die Recheneinheiten übertragen werden können.
2.  **Das flache Dach (rechengebunden)**: Erreichbare Leistung = maximale Rechenkapazität. Auf dieser Leitung werden Daten schnell genug bereitgestellt, damit die Recheneinheiten mit maximaler Kapazität arbeiten können.

Der Punkt, an dem sich diese beiden Linien schneiden, wird als _Kamm-Punkt_ bezeichnet. Sie definiert die Mindest-OI, die für eine Arbeitslast erforderlich ist, um die maximale Hardwareauslastung zu erreichen.

Im vorherigen Bild befindet sich Algo 1 im Bereich des Diagramms mit der Bezeichnung „memory bound“ (speichergebunden) und nutzt die Recheneinheiten nicht vollständig. Im Gegensatz dazu hat Algo 2 einen höheren OI und befindet sich im Bereich des Diagramms mit der Bezeichnung „compute bound“. Um Algo 1 zu optimieren, würde ein Nutzer versuchen, den Algorithmus so zu ändern, dass mehr Berechnungen mit weniger Datenbewegungen durchgeführt werden (OI erhöhen), um die Leistung nach rechts in Richtung des Ridge-Punkts zu verschieben.

### Beispiele für Arbeitslasten mit niedrigem und hohem OI

*   **Geringe HBM-Betriebsintensität (speichergebunden)**: Arbeitslasten wie elementweise Operationen (Aktivierungsfunktionen wie ReLU oder GeLU), Layer-Normalisierung und autoregressive Dekodierung (Batchgröße = 1 für die Inferenz).
*   **Hohe HBM-Betriebsintensität (Compute-gebunden)**: Arbeitslasten wie GEMMs oder Convolutional Neural Networks mit großen Batches. Bei der Matrixmultiplikation werden abgerufene Daten viele Male wiederverwendet (Zeilen werden mit Spalten multipliziert). Daher ist die OI sehr hoch und die Arbeitslasten fallen unter die flache Rechenleistungsgrenze.

Modell-Benchmarking
-------------------

Beim Modell-Benchmarking wird die tatsächliche Modellleistung gemessen. Mit Benchmarks für Training und Inferenz können Sie die Leistung beliebter Modelle zu einem bestimmten Zeitpunkt vergleichen.

In der folgenden Tabelle werden die Erkenntnisse verglichen, die Sie aus dem Modell-Benchmarking für Trainings- und Inferenzarbeitslasten gewinnen können:

| Insight | Trainingsarbeitslasten | Inferenzarbeitslasten |
| --- | --- | --- |
| Skalieren | Oft sind Tests in größerem Maßstab erforderlich (mindestens 10.000 Chips, bei den größten Modellen bis zu 100.000 Chips). Bietet Einblicke in verteilte Arbeitslasten, Kommunikationsaufwand und Netzwerklimits auf Clusterebene. | Häufig kleinere Tests (1–64 Chips). Sie erhalten Einblicke, wie die Plattform mit gleichzeitigen Nutzern und einer schnellen Steigerung der Last umgeht. |
| Leistung | Oft rechenintensiver. Misst die pro Sekunde und Chip verarbeiteten Tokens und die FLOP-Auslastung des Modells (Model FLOPS Utilization, MFU). | Latenzempfindlich. Misst die Zeit bis zum ersten Token (Time to First Token, TTFT), die Latenz zwischen den Tokens und die Gesamtzahl der Tokens, die pro Sekunde und Nutzer generiert werden. |
| Latenz | E/A- und Interconnect-Latenz, die Speicherengpässe beim Laden großer Datasets und die Netzwerklatenz zwischen Knoten bei synchronen Gradientenaktualisierungen aufzeigen. | End-to-End-Antwortlatenz, die Wartezeiten in der Warteschlange, Endpunktlatenz und nutzerseitige Wartezeiten hervorhebt. |

### Benchmarking von Trainings

Um die tatsächliche Hardware- und Netzwerkeffizienz zu ermitteln, müssen Sie die Leistung auf einen einzelnen, vergleichbaren Messwert für alle Beschleuniger normalisieren: **Tokens pro Sekunde pro Chip (TPS/Chip)**. Dabei muss eine bestimmte, repräsentative Modellarchitektur konstant bleiben. Wenn Sie nachverfolgen, wie sich die TPS/Chip-Rate verhält, wenn Sie die Größe eines Clusters skalieren, können Sie die versteckte „Skalierungssteuer“ des Systems aufdecken.

Um die Leistung mit den Kosten der Beschleuniger zu normalisieren, teilen Sie die TPS/Chip durch die Kosten jedes Chips, um **TPS/Chip/$** zu erhalten. Dies ist ein weiterer Vergleichspunkt.

Bewerten Sie für jedes Modell, das einem Benchmark unterzogen wird, Folgendes:

| Benchmark | Erklärung |
| --- | --- |
| Referenz-TPS/Chip und ‑TPS/Chip/$ messen | 
Führen Sie das Zielmodell auf dem kleinsten geeigneten Cluster aus. Erfassen Sie den globalen Trainingsdurchsatz (Gesamtzahl der pro Sekunde verarbeiteten Tokens) und teilen Sie ihn durch die Anzahl der Chips, um den Baseline-TPS/Chip zu ermitteln. Teilen Sie durch die Beschleunigerkosten, um TPS/Chip/$ zu erhalten.

Alternativ können Sie die FLOP-Auslastung des Modells (MFU) während des Trainings beobachten, um das Verhältnis des beobachteten Durchsatzes zum theoretischen Maximum zu messen. Das ist nützlich, um zu verstehen, wie nah die Hardwareleistung am Benchmarking ist. Allerdings ist der Vergleich von Chip zu Chip nicht so aussagekräftig wie bei TPS/Chip.

 |
| Beeinträchtigung der Skalierung bewerten | Skalieren Sie den Cluster auf 256, 1.024 und 4.096 Chips und führen Sie genau dasselbe Modell aus. Berechnen Sie die TPS/Chip-Werte für jede Skalierung neu. |
| [Goodput](https://cloud.google.com/blog/products/ai-machine-learning/goodput-metric-as-measure-of-ml-productivity?hl=de) berücksichtigen | 

Die Roh-TPS/Chip ist nur wichtig, wenn das Modell tatsächlich lernt. Berechnen Sie den Goodput, um die Rate der nützlichen Berechnungen zu messen, die den Trainingsstatus eines LLM direkt vorantreiben. Dabei werden Zeit und Energie, die durch Hardwarefehler, Netzwerkunterbrechungen oder Checkpoint-Wiederherstellungen verschwendet werden, explizit ausgeschlossen.

Bei der Bewertung von KI-Beschleunigern im großen Maßstab liefert der Goodput ein realistischeres Bild des Return on Investment als der theoretische Durchsatz, da er zeigt, wie effektiv die Hardware die Leistung in realen, fehleranfälligen Clustern aufrechterhält.

 |

In der folgenden Tabelle sind empfohlene Modelle für das Benchmarking für das Training aufgeführt:

| Größe | Architektur | Modell | Begründung |
| --- | --- | --- | --- |
| Klein (8 Mrd.) | Vollbesetzt | [Llama 3.1 8B](https://huggingface.co/meta-llama/Llama-3.1-8B) | Llama 3 ist seit mehreren Jahren ein Standardmodell, das bei Benchmarking-Standards wie MLPerf beliebt ist. |
| Mittel (70 Mrd. Parameter) | Vollbesetzt | [Llama 3.1 70B](https://huggingface.co/meta-llama/Llama-3.1-70B) | Llama 3 ist seit mehreren Jahren ein Standardmodell, das bei Benchmarking-Standards wie MLPerf beliebt ist. |
| Groß (671 B) | MoE | [DeepSeek-V3 671B](https://huggingface.co/deepseek-ai/DeepSeek-V3) | DeepSeek-V3 hat 2025 einen neuen Standard für Größe und Leistung gesetzt und ist auf vielen Multi-Chip-Plattformen gut optimiert. |

#### Beispiel: Normalisieren auf Leistung pro Euro

Stellen Sie sich einen Benchmark-Vergleich zwischen Chip\_A, Chip\_B und Chip\_C vor, bei dem Sie Trainingsbenchmarks für gängige Modelle ausgeführt haben, um die Leistung in TPS zu sehen. Anschließend sehen Sie sich das Verhältnis der Leistung von Chip A zur Leistung von Chip B und Chip C für dasselbe Modell an:

| Benchmark | Chip\_A TPS als Bruchteil von Chip\_B TPS | Chip\_A TPS als Bruchteil von Chip\_C TPS |
| --- | --- | --- |
| Klein und dicht: Llama 3.1 8B | 0,82 | 0,62 |
| MoE: Mixtral 8x7B | 0.72 | 0,55 |
| Groß und dicht: Llama 3.1 405B | 0,77 | 0,61 |
| Großes MoE: DeepSeek-V3 | 0,85 | 0,62 |
| Durchschnittswert | 0,79 | 0,60 |

Anhand der Daten in der vorherigen Tabelle lässt sich erkennen, dass die Leistung von Chip\_A durchschnittlich 0,79 der Leistung von Chip\_B und 0,60 der Leistung von Chip\_C entspricht. Ohne weitere Informationen wäre die Schlussfolgerung, dass Chip\_C überlegen ist.

Wenn Chip\_A jedoch 100 $, Chip\_B 180 $und Chip\_C 200 $kostet, ändert sich das Ergebnis durch die Normalisierung auf die Leistung pro Dollar (perf/$):

| Benchmark | Chip\_A perf/$ als Bruchteil von Chip\_B perf/$ | Chip\_A perf/$ als Bruchteil von Chip\_C perf/$ |
| --- | --- | --- |
| Klein und dicht: Llama 3.1 8B | 1,48 | 1.24 |
| MoE: Mixtral 8x7B | 1,30 | 1.10 |
| Groß und dicht: Llama 3.1 405B | 1,39 | 1.22 |
| Großes MoE: DeepSeek-V3 | 1,53 | 1.24 |
| Durchschnittswert | 1.42 | 1.20 |

Wenn Sie die Leistung pro Dollar als Vergleichspunkt betrachten, übertrifft Chip\_A Chip\_B um durchschnittlich 42% und Chip\_C um durchschnittlich 20%.

### Inferenz-Benchmarking

Das Training ist eine massive Vorabinvestition, das Bereitstellen (und damit die Inferenz) stellt jedoch langfristige Betriebsausgaben dar. Ein höherer TPS/Chip bedeutet, dass Sie weniger physische Server benötigen, um dieselben betrieblichen Arbeitslasten zu unterstützen. Dadurch werden der Energieverbrauch und die Größe des Rechenzentrums drastisch reduziert.

Bei der Inferenz besteht das Ziel darin, den Durchsatz zu maximieren, ohne die Latenzanforderungen zu verletzen, um eine reaktionsschnelle Nutzererfahrung zu gewährleisten. Durch die Standardisierung der Bewertung auf TPS/Chip für ein festes Modell können Sie die Leistung verschiedener Chips direkt vergleichen.

Beim Benchmarking der Inferenz müssen Sie die Leistung normalisieren, indem Sie TPS/Chip/$ berechnen:

| Benchmark | Erklärung |
| --- | --- |
| Latenz-SLA festlegen | 
Legen Sie zuerst ein strenges SLA für die Nutzerfreundlichkeit fest. Beispiel: Eine vorhersagbare Tail-Latenz (P99) von 100 Millisekunden. Messen Sie die Nutzerfreundlichkeit der Reaktionsfähigkeit mit TTFT (weniger als 500 ms) und TPOT (Time Per Output Token).

 |
| Batchgröße erhöhen | Erhöhen Sie die Anzahl der gleichzeitigen Anfragen (Batchgröße) auf der Hardware nach und nach. Mit zunehmender Batchgröße steigt der Durchsatz, aber die Latenz nimmt schließlich ab. |
| Maximale anhaltende TPS/Chip aufzeichnen | 

Wenn die Hardware gegen das P99-Latenz-SLA verstößt, beenden Sie den Vorgang. Notieren Sie sich den Gesamtdurchsatz des Systems bei dieser Batchgröße und teilen Sie ihn durch die Anzahl der Chips. Dies ist der Wert für TPS/Chip.

Einige Beschleuniger für allgemeine Zwecke haben bei hoher Batch-Auslastung Probleme mit der „Tail-Latenz“ (zufällige Spitzen bei der Verarbeitungszeit). Betreiber müssen sie daher bei geringerer Auslastung betreiben, um die Nutzer zufriedenzustellen.

Achten Sie darauf, die beiden unterschiedlichen Phasen des Prefill (rechenintensiv) und des Decodierens (speicherbandbreitenintensiv) zu messen.

 |
| Gesamtbetriebskosten pro Tausend oder Million Tokens berechnen | Teilen Sie die amortisierten Kapital- und Energiekosten eines Chips durch den maximalen nachhaltigen TPS/Chip. Damit wird der technische Benchmark in einen finanziellen Messwert umgewandelt, der die tatsächlichen Kosten aufzeigt. |

In der folgenden Tabelle sind empfohlene Modelle für Benchmarks für die Inferenz aufgeführt:

| Größe | Architektur | Modell | Begründung |
| --- | --- | --- | --- |
| Klein (8 Mrd.) | Vollbesetzt | [Llama 3.1 8B](https://huggingface.co/meta-llama/Llama-3.1-8B) | Llama 3 ist seit mehreren Jahren ein Standardmodell, das bei Benchmarking-Standards wie MLPerf beliebt ist. |
| Mittel (70 Mrd. Parameter) | Vollbesetzt | [Llama 3.1 70B](https://huggingface.co/meta-llama/Llama-3.1-70B) | Llama 3 ist seit mehreren Jahren ein Standardmodell, das bei Benchmarking-Standards wie MLPerf beliebt ist. |
| Groß (480 B) | MoE | [Qwen3 Coder 480B](https://huggingface.co/Qwen/Qwen3-Coder-480B-A35B-Instruct) | Qwen3 480B ist ein führendes OSS-Codierungsmodell. |

Nächste Schritte
----------------

*   [Cloud TPU-Architektur](https://docs.cloud.google.com/tpu/docs/system-architecture-tpu-vm?hl=de)
*   [Cloud TPU-Leistungsrezepte](https://github.com/AI-Hypercomputer/tpu-recipes)
*   [vLLM-TPU-Dokumentation](https://docs.vllm.ai/projects/tpu/en/latest/)
*   [MaxText zum Trainieren von Modellen auf TPUs](https://maxtext.readthedocs.io/en/latest/index.html)
*   [Roofline-Modell auf Wikipedia](https://wikipedia.org/wiki/Roofline_model)
