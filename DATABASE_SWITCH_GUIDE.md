# Switch to PostgreSQL Database with International Teams

## Current Situation

Your application currently connects to **MySQL** (`opinion_test` database) which has old/test data.

Your **clean international teams data** (19 teams, 95 players with images) is in the **PostgreSQL** database on Neon.

## How to Switch

### Option 1: Update application.properties (Recommended)

Edit `src/main/resources/application.properties`:

**Change from:**
```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/opinion_test
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

**Change to:**
```properties
# PostgreSQL (Neon) - with international teams
spring.datasource.url=jdbc:postgresql://ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_Zp2Y8UmTSeJB
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Option 2: Use Spring Profiles

Create separate profile files:

**src/main/resources/application-dev.properties** (MySQL for development):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/opinion_test
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

**src/main/resources/application-prod.properties** (PostgreSQL with real data):
```properties
spring.datasource.url=jdbc:postgresql://ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_Zp2Y8UmTSeJB
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

Then run with:
```bash
# Use PostgreSQL (production data)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Use MySQL (development data)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## After Switching

1. **Restart the application**:
   ```bash
   # Kill current process
   lsof -ti:8081 | xargs kill -9

   # Start with new database
   ./mvnw spring-boot:run
   ```

2. **Verify it's using PostgreSQL**:
   ```bash
   curl http://localhost:8081/teams | grep -c "T1\|Gen.G\|G2"
   ```
   Should show your 19 international teams!

3. **Open the test page**:
   ```bash
   open test-images.html
   ```

## Database Comparison

| Database | Location | Teams | Players | Image Paths |
|----------|----------|-------|---------|-------------|
| **PostgreSQL (Neon)** | Cloud | 19 international teams | 95 players | ✅ Correct paths (/images/...) |
| **MySQL (Local)** | localhost | Many test teams | Mixed | ❌ Old/incorrect paths |

## Why Switch?

✅ **PostgreSQL has:**
- 19 teams from MSI 2025 & Worlds 2025
- 95 players with proper roles (Top, Jungle, Mid, ADC, Support)
- Correct image paths pointing to local files
- Clean, production-ready data

❌ **MySQL has:**
- Old test data
- Incorrect image paths
- Mixed/incomplete player data

## Quick Switch Command

```bash
# In application.properties, replace the datasource section with:
spring.datasource.url=jdbc:postgresql://ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_Zp2Y8UmTSeJB
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Then restart
lsof -ti:8081 | xargs kill -9 && ./mvnw spring-boot:run
```
