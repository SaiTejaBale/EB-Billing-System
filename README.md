EB Billing System – Test Automation Suite

This project is a fork of the [EB-Billing-System](https://github.com/ShashwanthN/EB-Billing-System) and includes a custom-built Test Automation Suite developed using Java, Selenium WebDriver, TestNG, and Maven.

It was created to demonstrate test automation strategy, framework design, and practical implementation for evaluating key workflows in a web-based billing system — including login, billing logic validation, and historical bill verification.



✅ Included Test Cases

| Test Case                  | Description |
|---------------------------|-------------|
| `LoginTest`               | Verifies valid login using correct admin credentials. |
| `BillCalculationTest`     | Validates dynamic electricity bill calculation based on unit usage. |
| `PreviousBillsTest`       | Confirms historical bills are displayed accurately and match expectations. |



🧰 Tech Stack

- Java 17+
- Selenium WebDriver
- TestNG
- Maven (Build Tool)
- Page Object Model (Framework Pattern)
- ExtentReports (Screenshots & Reports)
- Git (Version Control)


🏗️ Project Structure

src/
├── test/
│ ├── java/
│ │ ├── tests/ # Test classes
│ │ ├── pages/ # Page objects
│ │ └── base/ # Driver setup and teardown
testng.xml # Test suite runner
test-output/ # Screenshots + reports

## 🚀 How to Run

1. Clone the repo:

   ```bash
   git clone https://github.com/SaiTejaBale/EB-Billing-System.git
   cd EB-Billing-System

2. Switch to the test branch:

git checkout test-automation-suite

3. Open in IntelliJ (or your preferred IDE)

4. Run tests via:

testng.xml file

or directly from the test classes

💡 Why I Built This
This suite was developed to:

Show how test automation can be built, maintained, and scaled efficiently

Highlight my ability to build reusable, maintainable frameworks

Demonstrate initiative beyond just manual QA or task-based automation

Serve as a practical case for roles like Test Lead at El Paso Electric, where internal ownership and framework optimization are valued
