# bank

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                                   | Description                                                                        |
| -----------------------------------------------------------------------|------------------------------------------------------------------------------------ |
| [Routing](https://start.ktor.io/p/routing)                             | Provides a structured routing DSL                                                  |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library                     |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)     | Provides automatic content conversion according to Content-Type and Accept headers |
| [Postgres](https://start.ktor.io/p/postgres)                           | Adds Postgres database to your application                                         |
| [Exposed](https://start.ktor.io/p/exposed)                             | Adds Exposed database to your application                                          |

## Requirements

All you need to run is:
- Java
- Docker

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| ------------------------------|--------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `./gradlew run`               | Run the server                                                       |
| `docker-compose up -d`        | Run PostgreSQL local                                                 |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [DefaultDispatcher-worker-1] INFO  Application - Responding at http://127.0.0.1:8080
```

## Testing with Postman

To test with postman download and import this [postman collection](https://drive.google.com/file/d/10Kz87iOOZtqSMgtyMNy3Thzbj8Mr_DEM/view?usp=sharing).

## API

_Add Account_
```
curl --location 'http://localhost:8080/accounts' \
--header 'Content-Type: application/json' \
--data '{
    "accountId": "1"
}'
```

_Get Accounts_
```
curl --location 'http://localhost:8080/accounts'
```

_Get Balances by Account_
```
curl --location 'http://localhost:8080/balances/account/1'
```

_Update Account Balance_
```
curl --location --request PUT 'http://localhost:8080/balances/account/1/balance/CASH/amount/5000'
```

_Add Transaction_
```
curl --location 'http://localhost:8080/transactions' \
--header 'Content-Type: application/json' \
--data '{
	"account": "1",
	"amount": 100.00,
	"mcc": "invalid",
	"merchant": "PADARIA DO ZE               SAO PAULO BR"
}'
```

_Get Account Transactions_
```
curl --location 'http://localhost:8080/transactions/account/1'
```

_Add Merchant_
```
curl --location 'http://localhost:8080/merchants' \
--header 'Content-Type: application/json' \
--data '{
    "name": "IFOOD",
    "mcc": "5812"
}'
```

_Get Merchants_
```
curl --location 'http://localhost:8080/merchants'
```

