# 🫱🏻‍🫲🏽 SAATHI
> **Your Voice. Your Problem. Your Assistance.**

SAATHI is a voice-first, multilingual AI public-assistance navigator for Indian citizens. Instead of searching complex portals for government schemes, citizens state their real-life problems in their spoken language, and SAATHI guides them to the right assistance, required documents, and official application sources.

---

## 🛠 Tech Stack

| Layer | Technology & Frameworks |
|---|---|
| **Mobile App (Android)** | Kotlin, Jetpack Compose, Material 3, MVVM, Coroutines, StateFlow, Retrofit, OkHttp |
| **Backend & APIs** | Python, FastAPI, Pydantic, SQLAlchemy, Docker, Pytest |
| **Databases** | SQLite (User Profiles & History), Qdrant (Vector Database for Semantic RAG) |
| **AI & Voice Engine** | **Rime Labs AI** (Ultra-low latency Text-to-Speech & Speech-to-Text), LLM Orchestration, Semantic Chunking |

---

## 🎙️ Hands-Free "Wake Up" Hotword Engine

SAATHI incorporates a background listening engine to make the platform accessible without physical interaction:

* **Wake-Word Listener:** Runs locally on-device to detect `"Wake up SAATHI"` or `"Hi SAATHI"` phrases without requiring screen taps.
* **Immediate Audio Pipeline:** Wakes the assistant and opens a live bidirectional voice stream powered by **Rime** as soon as the hotword is uttered.
* **Low-Power Audio Buffer:** Utilizes lightweight Android native audio APIs to conserve battery life during background listening.

---

## 🗺️ Future Google Maps Integration

To convert scheme awareness into physical real-world execution, SAATHI will deeply integrate Google Maps Platform services:

1. **Nearest Service Center Discovery (Google Places API)**
   * Locates local Common Service Centres (CSCs), Tehsil offices, district healthcare centers, and public offices relevant to the specific scheme.
2. **Location-Based Jurisdiction Filtering (Google Geocoding API)**
   * Translates real-time GPS coordinates into administrative boundaries (State, District, Block) to automatically filter eligible regional schemes.
3. **Turn-by-Turn Navigation & Maps SDK (Google Maps SDK & Directions API)**
   * Embeds interactive maps in the application, rendering visual routes and transit directions to application submission points.
4. **Distance & Travel Time Estimation (Google Distance Matrix API)**
   * Ranks centers by live travel time and traffic conditions, ensuring users are directed to accessible facilities.

---

## Key Capabilities
1.Problem-First Navigation: Converts natural voice inputs (such as "I want to start a food stall" or "I need help paying my mother's hospital bills") into           tailored, eligible welfare schemes without requiring domain knowledge.

2. Multilingual & Voice-Native: Designed for natural, spoken conversations across Hindi, English, Hinglish, and regional languages.

3. Progressive Interviewing: Asks minimal, targeted follow-up questions to clarify eligibility criteria (e.g., location, income bracket, occupation) rather than      presenting overwhelming forms.

4. Explainable & Grounded Guidance: Translates dense legal jargon into clear steps, outlining why a scheme matches, what documents are required, and where to       apply —backed directly by verified official sources.

5. Action-Oriented Location Intelligence: Bridges information with physical execution by helping users discover nearest local service centers (CSCs), Tehsil          offices, and government facilities.

# Install dependencies and start server
pip install -r requirements.txt
uvicorn app.main:app --reload
