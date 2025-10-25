#!/usr/bin/env python3
"""Debug script to see what's actually on OddsPortal page"""

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time

def main():
    chrome_options = Options()
    chrome_options.add_argument('--headless')
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-dev-shm-usage')
    chrome_options.add_argument('user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36')

    driver = webdriver.Chrome(options=chrome_options)

    try:
        url = "https://www.oddsportal.com/esports/league-of-legends/"
        print(f"Loading {url}...")
        driver.get(url)

        # Wait longer for dynamic content
        time.sleep(5)

        # Save page source
        page_source = driver.page_source
        with open('/tmp/oddsportal_page.html', 'w', encoding='utf-8') as f:
            f.write(page_source)
        print("Page source saved to /tmp/oddsportal_page.html")

        # Print page title
        print(f"Page title: {driver.title}")

        # Try to find any divs
        all_divs = driver.find_elements('tag name', 'div')
        print(f"Total div elements: {len(all_divs)}")

        # Check for common class patterns
        patterns = ['event', 'match', 'odds', 'game', 'participant', 'team']
        for pattern in patterns:
            elements = driver.find_elements('css selector', f'[class*="{pattern}"]')
            print(f"Elements with '{pattern}' in class: {len(elements)}")
            if elements:
                # Show first few classes
                for elem in elements[:3]:
                    print(f"  - class: {elem.get_attribute('class')}")

    finally:
        driver.quit()

if __name__ == "__main__":
    main()
