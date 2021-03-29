## Android Mobile Application - Stock Watch
Uses: Internet, RecyclerView, Option-Menus, Multiple AsyncTasks, JSON Data, Swipe-Refresh, Dialogs, Implied Intents

### App Highlights:

* This app allows the user to display a sorted list of selected stocks. List entries include the stock symbol, company name, the current price, the daily price change amount and price percent change.

* Selected stock symbols and the related names should be stored in a JSON file on the device.

* A Stock class should be created to represent each individual stock in the application. Required data includes: Stock Symbol (String), Company Name (String), Price (double), Price Change (double), and Change Percentage (double).

* Clicking on a stock entry opens a browser displaying the Market Watch web page for the selected stock

* Swipe-Refresh (pull-down) refreshes stock data

### Detail

* Adding a stock – when only one stock matched the search symbol/name search string (NOTE: The Stock Selection dialog should only allow capital letters)
* Adding a stock – multiple stocks matched the search string (Stock Selection dialog should only allow capital letters, stock selection dialog should display the stock symbol and company name)
* Adding a stock with no Network Connection – test using “Airplane Mode” (You can show no buttons on the error dialog, or an “Ok” button – either is fine)
* Adding a stock – specified stock is a duplicate (Stock Selection dialog should only allow capital letters, You can show no buttons on the error dialog, or an “Ok” button – either is fine)
* Adding a stock – specified stock is not found (Stock Selection dialog should only allow capital letters, You can show no buttons on the error dialog, or an “Ok” button – either is fine)
* Swipe-Refresh (pull-down) reloads (re-downloads) all stock financial data
* Swipe-Refresh attempt with no network connection (You can show no buttons on the error dialog, or an “Ok” button – either is fine)
* Long-Press on a stock to delete it
* Tap on a stock to open the MarketWatch.com website entry for the selected stock
 
