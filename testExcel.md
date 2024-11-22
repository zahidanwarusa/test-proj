Create an Excel file named `TestCases.xlsx` and save it in `src/test/resources/` with the following data:

Sheet name: "Sheet1"

| TestId | Endpoint     | Method | RequestBody                                             | Headers                                          | ExpectedStatus | ExpectedResponse |
|--------|-------------|--------|--------------------------------------------------------|--------------------------------------------------|----------------|------------------|
| TC001  | /posts/1    | GET    |                                                        | {"Content-Type": "application/json"}              | 200            | $.id            |
| TC002  | /posts      | POST   | {"title": "foo","body": "bar","userId": 1}            | {"Content-Type": "application/json"}              | 201            | $.id            |
| TC003  | /posts/1    | PUT    | {"id": 1,"title": "updated","body": "test","userId": 1}| {"Content-Type": "application/json"}              | 200            | $.title         |
| TC004  | /posts/1    | DELETE |                                                        | {"Content-Type": "application/json"}              | 200            |                 |
| TC005  | /users/1    | GET    |                                                        | {"Content-Type": "application/json"}              | 200            | $.name          |

Important formatting notes:
1. First row is the header row
2. ExpectedStatus should be numbers (not text)
3. Empty cells should be truly empty (not containing spaces)
4. Save as .xlsx format (not .xls)
