import json
import re
import os

files_to_autowire = [
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\inventory\service\StockTransferService.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\controller\SettlementController.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\service\SettlementDisputeService.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\service\SettlementPaymentService.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\service\SettlementReconciliationService.java",
    r"c:\Users\user\Desktop\MobileApplication\backend\src\main\java\com\buysell\modules\settlement\service\SettlementService.java",
]

for filepath in files_to_autowire:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
        
        # Remove @RequiredArgsConstructor
        content = re.sub(r'@RequiredArgsConstructor\s*\n', '', content)
        content = re.sub(r'import lombok\.RequiredArgsConstructor;\s*\n', '', content)
        
        # Replace private final Service/Repo with @Autowired private Service/Repo
        # Note: We need to make sure we also import @Autowired
        content = re.sub(r'(\s+)private final ([A-Za-z0-9_<>]+ [a-zA-Z0-9_]+;)', r'\1@org.springframework.beans.factory.annotation.Autowired\1private \2', content)
        
        with open(filepath, 'w') as f:
            f.write(content)

print("Services fixed")
