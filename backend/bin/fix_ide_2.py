import json
import re
import os

files_to_suppress = [
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\inventory\event\StockTransferCompletedEvent.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\dto\CreateReconciliationRequest.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\dto\CreateSettlementDisputeRequest.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\dto\CreateSettlementPaymentRequest.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\dto\ResolveDisputeRequest.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\dto\ShopLedgerSummaryResponse.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\entity\ReconciliationRecord.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\entity\SettlementDispute.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\entity\SettlementPayment.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\entity\ShopSettlement.java",
]

for filepath in files_to_suppress:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
        
        # Add @SuppressWarnings("unused") to private fields that do not already have it
        lines = content.split('\n')
        new_lines = []
        for line in lines:
            if re.match(r'^\s*private ', line) and not '@SuppressWarnings' in line:
                indent = re.match(r'^(\s*)', line).group(1)
                new_lines.append(indent + '@SuppressWarnings("unused")')
            new_lines.append(line)
            
        with open(filepath, 'w') as f:
            f.write('\n'.join(new_lines))


# Fix tests
def remove_unused(filepath, to_remove_vars, to_remove_methods):
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
            
        for var in to_remove_vars:
            content = re.sub(r'\s*@[A-Za-z]+(\([^)]+\))?\s*private\s+[A-Za-z0-9_<>]+ ' + var + r'\s*;', '', content)
            
        for meth in to_remove_methods:
            # We will just suppress unused on the method if it's setup
            content = re.sub(r'(\s+)(void ' + meth + r'\(\)\s*\{)', r'\1@SuppressWarnings("unused")\1\2', content)
            
        # For throwable ignored, we assign the result to a variable and suppress it, or just add @SuppressWarnings
        content = re.sub(r'assertThrows', r'var ignored = assertThrows', content)
        
        with open(filepath, 'w') as f:
            f.write(content)

remove_unused(
    r"c:\Users\user\Desktop\MobileApplication\backend\src\test\java\com\buysell\modules\settlement\SettlementIntegrationTest.java",
    ["currentUserService"],
    ["setup"]
)

remove_unused(
    r"c:\Users\user\Desktop\MobileApplication\backend\src\test\java\com\buysell\modules\settlement\SettlementPaymentServiceTest.java",
    ["auditService"],
    ["setup"]
)

remove_unused(
    r"c:\Users\user\Desktop\MobileApplication\backend\src\test\java\com\buysell\modules\settlement\SettlementServiceTest.java",
    ["currentUserService", "auditService"],
    ["setup"]
)

print("Entities, DTOs, and Tests fixed")
