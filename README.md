## 📖 Table of Contents

1. [🔗 Services & Access Information](#-services--access-information)
2. [📝 Environment Variables](#-environment-variables)
3. [💻 Requirements](#-requirements)
4. [🚀 Running the Services](#-running-the-services)
5. [🛠 Setting Up PostgreSQL in pgAdmin](#-setting-up-postgresql-in-pgadmin)
6. [📊 Monitoring Services](#-monitoring-services)
7. [👥 Project Contributors](#-project-contributors)

---

## 🔗 Services & Access Information

| 🛠 Service                 | 🌍 URL / Port                                                                                  | 👤 Username       | 🔑 Password  |
|----------------------------|------------------------------------------------------------------------------------------------|-------------------|--------------|
| **Spring Boot**            | `http://localhost:8080`                                                                        | `admin`           | `admin`      |
| **Keycloak**               | [http://localhost:8181](http://localhost:8181)                                                 | `admin`           | `admin`      |
| **pgAdmin**                | [http://localhost:8282](http://localhost:8282)                                                 | `admin@gmail.com` | `admin`      |
| **Prometheus**             | [http://localhost:9090](http://localhost:9090)                                                 | -                 | -            |
| **Grafana**                | [http://localhost:9191](http://localhost:9191)                                                 | -                 | -            |
| **PostgreSQL**             | `localhost:5432`                                                                               | `admin`           | `admin`      |
| **Keycloak<br>PostgreSQL** | `localhost:2345`                                                                               | `admin`           | `admin`      |
| **MinIO UI**               | `Run "docker logs minio", find the WebUI link`                                                 | `admin`           | `adminadmin` |
| **MinIO API**              | `http://localhost:9000`                                                                        | `admin`           | `adminadmin` |
| **Swagger UI**             | [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/) | -                 | -            |
| **React**                  | [http://localhost:3000](http://localhost:3000)                                                 | -                 | -            |

---

## 📝 Environment Variables

- The environment variables are stored in the `.env` file:
```sh
KC_ADMIN_USERNAME=admin
KC_ADMIN_PASSWORD=admin

SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin
SPRING_USERNAME=admin
SPRING_PASSWORD=admin

PGADMIN_DEFAULT_EMAIL=admin@gmail.com
PGADMIN_DEFAULT_PASSWORD=admin

POSTGRES_USERNAME=admin
POSTGRES_PASSWORD=admin

KC_POSTGRES_USERNAME=admin
KC_POSTGRES_PASSWORD=admin

MINIO_USERNAME=admin
MINIO_PASSWORD=adminadmin
MINIO_SERVER_URL=http://localhost:9000

GOOGLE_APPLICATION_CREDENTIALS=src/main/resources/google.json
GOOGLE_CLOUD_PROJECT_ID=forum-478311
```
Make sure you paste in your Google Vision AI API key in the specified path and set the correct project ID.
Go to localhost:9001, login with the default MinIO credentials, create a bucket named `forum`, set the bucket policy to `public` instead of `private`

---

## 💻 Requirements

Before running the project, ensure you have the following installed:

- **Java 17+**
- **Maven**
- **Docker**:
  - [Docker Desktop (Windows and Apple)](https://www.docker.com/products/docker-desktop/)
  - [OrbStack (Apple only, recommended)](https://orbstack.dev/download)
- **Node.js 18+** (for React frontend)
- **npm** or **yarn** (package manager for JavaScript)

---

## 🚀 Running the Services

### 🚢 Start All Services

```sh
docker compose up
```

### 🚀 Start Spring Boot Backend

```sh
./mvnw spring-boot:run
```

### 🚀 Start React Frontend

Navigate to the React project directory:

```sh
cd frontend
```

Install dependencies:

```sh
npm install
# or
yarn install
```

Start the development server:

```sh
npm start
# or
yarn start
```

The React application will be available at [http://localhost:3000](http://localhost:3000)

---

## 🛠 Setting Up PostgreSQL in pgAdmin

### 1️⃣ Open pgAdmin

- Navigate to [http://localhost:8282](http://localhost:8282)
- Log in with:
  - **Username**: `admin@gmail.com`
  - **Password**: `admin`

### 2️⃣ Register a New Server

- Right-click **Servers** in the Browser panel → **Register** → **Server...**

### 3️⃣ Configure the First Server

- **General Tab**:
  - Name: `forum`
- **Connection Tab**:
  - Host: `host.docker.internal`
  - Port: `5432`
  - Username: `admin`
  - Password: `admin`

### 4️⃣ Register a Second Server

- Follow the same steps above.
- **General Tab**:
  - Name: `keycloak`

### 5️⃣ Configure the Second Server

- **Connection Tab**:
  - Port: `2345`
  - Host, Username, and Password remain the same.

---

## 📊 Monitoring Services

### 📡 Prometheus

1. Open [http://localhost:9090](http://localhost:9090)
2. Click **Status** → **Target health** to check monitored targets.

### 📉 Grafana

1. Open [http://localhost:9191](http://localhost:9191)
2. Click **Dashboards** on the sidebar.
3. Navigate to **Spring** → **Forum**.

---

## 👥 Project Contributors

- [UgniusSP](https://github.com/UgniusSP)
- [Antanas4](https://github.com/Antanas4)

---

## 📜 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.