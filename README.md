# ⚽ Table Soccer Coin Ledger API

A simple REST API to track coin debts between players after table soccer matches. Built with Java Spring Boot and using local JSON files for data persistence.

---

## ✅ Features

- Record match results (1v1 or 2v2)
- Automatically calculate debts: losers owe 1 coin to the winner in the same position
- Track and pay off coin debts
- View who owes whom and how much
- Store all data in a local JSON file
- View match history for any player
- Slack integration for recording matches using natural language
- AI-powered natural language processing for match recording

---

## 🧾 Requirements

### Functional Requirements

1. **Record Match Results**
   - 1v1 or 2v2
   - Example: A, B vs C, D — A & B win → C owes A 1 coin, D owes B 1 coin

2. **Pay Debt**
   - Example: D pays 1 coin to B → reduce debt from D to B

3. **Query Debts**
   - Query what a player owes or is owed

4. **List All Debts**
   - View the entire ledger of unresolved debts

5. **View Match History**
   - View all matches a specific player has participated in

6. **Slack Integration**
   - Record matches directly from Slack using natural language
   - Example: "A and B defeated C and D" → system records the match and updates debts

7. **Natural Language Processing**
   - AI-powered parsing of match results from text messages
   - Support for various phrasings and formats

---

## 📡 REST API Endpoints

### `POST /matches`
**Record a match result**

**Request Body:**
```json
{
  "team1": ["A", "B"],
  "team2": ["C", "D"],
  "winner": "team1"
}
```

---

### `GET /matches?player=A`
**Get match history for a specific player**

**Response Example:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "team1": ["A", "B"],
    "team2": ["C", "D"],
    "winner": "team1",
    "timestamp": "2025-04-23T14:23:45.123"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "team1": ["E", "F"],
    "team2": ["A", "G"],
    "winner": "team2",
    "timestamp": "2025-04-22T10:15:30.456"
  }
]
```

---

### `POST /debts/pay`
**Record a payment**

**Request Body:**
```json
{
  "from": "D",
  "to": "B",
  "amount": 1
}
```

---

### `GET /debts?player=A`
**Get debts related to player A**

**Response Example:**
```json
{
  "owes": [
    { "to": "B", "amount": 2 }
  ],
  "owed_by": [
    { "from": "C", "amount": 1 }
  ]
}
```

---

### `GET /debts/all`
**Get all unresolved debts**

**Response Example:**
```json
[
  { "from": "A", "to": "B", "amount": 2 },
  { "from": "C", "to": "A", "amount": 1 }
]
```

---

### `POST /slack/record-match`
**Record a match from Slack message**

**Request Body:**
```json
{
  "message": {
    "text": "A and B defeated C and D"
  }
}
```

**Response Example:**
```json
{
  "response_type": "in_channel",
  "text": "✅ Match recorded: A and B defeated C and D"
}
```

---

## 🗃️ Data Storage Format

### Debts (`data/debts.json`)

```json
[
  { "from": "A", "to": "B", "amount": 2 },
  { "from": "C", "to": "A", "amount": 1 }
]
```

- `from`: debtor
- `to`: creditor
- `amount`: positive integer (never negative)
- Opposing debts are netted out automatically (e.g. if A owes B 2, and B owes A 1 → stored as A owes B 1)

### Matches (`data/matches.json`)

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "team1": ["A", "B"],
    "team2": ["C", "D"],
    "winner": "team1",
    "timestamp": "2025-04-23T14:23:45.123"
  }
]
```

- `id`: unique identifier for the match
- `team1`, `team2`: arrays of player names
- `winner`: either "team1" or "team2"
- `timestamp`: when the match was recorded

---

## 🧱 Project Structure

```
table-soccer-ledger/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/tablesoccer/
│       │       ├── controller/
│       │       │   ├── MatchController.java
│       │       │   ├── DebtController.java
│       │       │   └── SlackController.java
│       │       ├── service/
│       │       │   ├── MatchService.java
│       │       │   ├── DebtService.java
│       │       │   └── LlmService.java
│       │       ├── model/
│       │       │   ├── Match.java
│       │       │   ├── MatchRequest.java
│       │       │   ├── Debt.java
│       │       │   └── SlackRequest.java
│       │       ├── repository/
│       │       │   ├── MatchHistory.java
│       │       │   └── DebtLedger.java
│       │       └── TableSoccerApplication.java
│       └── resources/
│           └── application.properties
├── data/
│   ├── debts.json
│   └── matches.json
├── README.md
└── build.gradle
```

---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Jackson (for JSON serialization)
- Local file I/O
- Gradle
- AI Natural Language Processing
- Slack API integration

---

## 🚀 Getting Started

```bash
git clone <repo-url>
cd table-soccer-ledger
./gradlew bootRun
```

The server will start at `http://localhost:8080`

### Slack Integration Setup

1. Create a Slack App in your Slack workspace
2. Set up a Slash Command pointing to `/slack/record-match`
3. Configure your Slack credentials in `application.properties`

---

## 🔮 Future Ideas

- Web frontend
- Login system
- Persistent database (MongoDB, PostgreSQL, etc.)
- Match statistics and player rankings
- Enhanced natural language capabilities
- Direct integration with table soccer hardware

---

## 👤 Author

Your Name or GitHub handle