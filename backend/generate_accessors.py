import json
import re
import os

files_to_fix = [
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

def capitalize_first(s):
    if not s: return s
    return s[0].upper() + s[1:]

for filepath in files_to_fix:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
        
        # Remove the suppress warnings we added
        content = re.sub(r'^\s*@SuppressWarnings\("unused"\)\s*\n', '', content, flags=re.MULTILINE)
        
        # Find all private fields
        fields = re.findall(r'^\s*private\s+(?:final\s+)?([A-Za-z0-9_<>\[\]]+)\s+([A-Za-z0-9_]+)(?:\s*=\s*[^;]+)?\s*;', content, re.MULTILINE)
        
        getters_setters = ""
        for field_type, field_name in fields:
            cap_name = capitalize_first(field_name)
            getters_setters += f"\n    public {field_type} get{cap_name}() {{\n        return this.{field_name};\n    }}\n"
            # Don't add setters for final fields
            # We'll just add setter for all non-final, but our regex captures both. Let's just do setter if we want to avoid "can be final"
            # Since it's a DTO/Entity we can add setters. But wait, if field has 'final', setter won't compile.
            # Let's re-parse to see if it's final
            pass
        
        # Wait, if we just want to avoid compilation errors, we can just write a better regex.
        lines = content.split('\n')
        new_lines = []
        methods_to_add = []
        for line in lines:
            m = re.match(r'^(\s*)private\s+(final\s+)?([A-Za-z0-9_<>\[\]]+)\s+([A-Za-z0-9_]+)(?:\s*=\s*[^;]+)?\s*;', line)
            if m:
                indent = m.group(1)
                is_final = bool(m.group(2))
                f_type = m.group(3)
                f_name = m.group(4)
                cap_name = capitalize_first(f_name)
                
                methods_to_add.append(f"{indent}public {f_type} get{cap_name}() {{\n{indent}    return this.{f_name};\n{indent}}}")
                if not is_final:
                    methods_to_add.append(f"{indent}public void set{cap_name}({f_type} {f_name}) {{\n{indent}    this.{f_name} = {f_name};\n{indent}}}")
                    
            new_lines.append(line)
            
        # Add methods before the last closing brace
        for i in reversed(range(len(new_lines))):
            if '}' in new_lines[i]:
                # Insert here
                new_lines.insert(i, "\n".join(methods_to_add))
                break
                
        with open(filepath, 'w') as f:
            f.write('\n'.join(new_lines))

print("Getters and Setters generated")
