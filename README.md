# FoodDevliveryApp

A desktop Online Food Delivery System** 

built with **JavaFX** and **Maven**. It simulates the core flow of a food ordering platform: account login/sign-up, browsing a menu, building a cart, placing an order, and an admin dashboard that shows every order placed by every customer in real time.

## Features

- **Login & Sign-Up** — seeded demo accounts, plus a sign-up dialog to register new Customer or Admin accounts on the fly.

- **Role-based routing** — Admin accounts land on the Admin Dashboard; Customer accounts land on the ordering screen.

- **Customer ordering screen**
  - Browse a menu of items (name, category, price)
  - Add items to a live shopping cart with a running total
  - Checkout form with input validation (empty fields, email format, empty cart)
  - Generates a formatted invoice after a successful order

- **Admin Dashboard**
  - Live table of every order placed by every customer (sourced from a single shared `OrderStore`)
  - Search/filter by order ID, customer name, or email
  - Click an order to view its full invoice
  - Running totals: order count and total revenue
  - Manual "Refresh" to re-read orders persisted by other sessions

- **Persistence** — orders are written to a simple pipe-delimited flat file (`orders_data.txt`) so an admin can see orders placed in a previous run of the app.

## Demo Accounts

| Username   | Password      | Role     |
|------------|---------------|----------|
| `prince`    | `prince123`    | Admin    |
| `linda` | `linda123` | Customer |

New accounts can also be created from the **Sign Up** dialog on the login screen.

## Tech Stack

- **Java 21** (JDK)
- **JavaFX 21** (Controls, FXML) for the UI
- **Maven** for build/dependency management
- **ControlsFX** / **FormsFX** (available for richer UI controls)
- No database — an in-memory `AuthService` for credentials and a file-backed `OrderStore` singleton for orders

## Getting Started

### Prerequisites

- JDK 21+
- Maven 3.9+ (or use the included `mvnw` / `mvnw.cmd` wrapper)

### Run the app

```bash
# macOS / Linux
./mvnw clean javafx:run

# Windows
mvnw.cmd clean javafx:run
```

This launches `Launcher`, which opens the **login screen**. Log in with one of the demo accounts above (or sign up) to reach the ordering screen or the admin dashboard.

### Build only

```bash
./mvnw clean package
```

## Project Structure

```
src/main/java/
├── com/delivery/                      # Core application
│   ├── AuthService.java                # In-memory credential store (login/sign-up, roles)
│   ├── LoginScreen.java                # Entry screen: login + sign-up dialog, routes by role
│   ├── FoodDeliveryApp.java             # Customer-facing menu, cart, and checkout screen
│   ├── AdminDashboard.java              # Admin screen: live order table, search, invoice detail
│   ├── OrderStore.java                 # Singleton shared order store (in-memory + flat-file persistence)
│   ├── Order.java                      # Immutable order/invoice snapshot; encode/decode for persistence
│   ├── MenuItem.java                   # Menu item model
│   ├── User.java                       # Abstract base user (encapsulation/abstraction)
│   └── Customer.java                   # Customer user, extends User (inheritance/polymorphism)
│
└── com/example/onlinefooddeliverysystem/
    └── Launcher.java                   # Real application entry point -> launches LoginScreen

module-info.java                        # Java module declaration (JavaFX, ControlsFX, FormsFX)
pom.xml                                  # Maven build config (dependencies, javafx-maven-plugin)
```

Orders placed in the app are appended to `orders_data.txt` (created in the project's working directory on first order) and reloaded automatically whenever the Admin Dashboard opens or "Refresh" is clicked.

## Object-Oriented Design Notes

- **Abstraction/Encapsulation** — `User` is an abstract base class exposing only getters/setters for its private fields.
- **Inheritance** — `Customer extends User`.
- **Polymorphism** — `Customer` overrides `getRoleDescription()`.
- **Singleton** — `OrderStore` guarantees a single shared source of truth for orders across the customer app and admin dashboard.

## Known Limitations

- Credentials and menu data are **not persisted** — they reset every time the app restarts (only orders survive, via `orders_data.txt`).
- Passwords are stored/compared in plain text — fine for a demo, not production-ready.
- Single flat-file persistence has no concurrent-write protection; it's intended for a single local user/session at a time.

## License

No license.