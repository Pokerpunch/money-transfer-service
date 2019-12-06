###About
This is a simple REST API service to showcase money transfers between accounts.

###Data model
```
User
 |
Account
 |
Transfer
``` 

###API
*Users*
* `GET api/users`
* `GET api/users/{id}`
* `POST api/users`
```
{
    "firstName": "string",
    "lastName": "string"
}
````

*Accounts*
* `GET api/accounts`
* `GET api/accounts/{id}`
* `POST api/accounts`
```
{
    "accountOwnerId": "long",
    "balanceMinor": "string"
}
````

*Transfers*
* `GET api/transfers`
* `GET api/transfers/{id}`
* `POST api/transfers`
```
{
  "originAccountId": "long",
  "destinationAccountId": "long",
  "amountMinor": "long"
}
```

### Assumptions, decisions and limitations

*Behavioural*
* Transfers do not take into account different currencies
* All amounts are in minor units corresponding to decimals with 2 decimal places 
(e.g. 1040 would be treated as 10.40)
* Users with duplicate names are allowed
* Deletion and modification of underlying entities via API calls is not supported
* A transfer can only be made from one account to another

*Technical*
* No dependency injection
* No pagination when loading entities
* APIs are not versioned
* All responses are in JSON
* Response objects expose all entity info for clarity
* Validation of a transfer attempt is done before and during the transfer transaction
* Dummy data is preloaded into memory upon startup

### Stack Used
* SparkJava
* Kotlin
* Mockito
* Hibernate
* H2 in-memory database
