# online-shopping

Development Branch Structure
* main
* dev (Development)
    * feature/function-name

Design Structure:

Entity:
1. Item
   1. ItemId 
   2. ItemName 
   3. ItemPictureUrl 
   4. ItemPrice
   4. UPC
2. Order
   1. OrderId
   2. ItemId
   3. Unit
   4. ItemPrice
   5. TotalPrice
   6. BuyerId
   7. OrderStatus
3. Payment
   1. PaymentId
   2. OrderId 
   2. TotalPrice
   3. PaymentStatus
4. Account
   1. AccountId
   2. AccountName
   3. AccountAddress
   4. Payment?

Repo:
1. ItemRepo
2. OrderRepo
3. PaymentRepo
4. Account Repo

Service:


