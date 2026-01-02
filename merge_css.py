import re
import os

RESTORE_PATH = "app_restore.css"
CURRENT_PATH = "src/main/resources/css/app.css"
OUTPUT_PATH = "src/main/resources/css/app.css"

def clean_selector(sel):
    # Remove comments
    sel = re.sub(r'/\*.*?\*/', '', sel, flags=re.DOTALL)
    # Normalize whitespace
    return ' '.join(sel.split())

def parse_blocks(text):
    blocks = []
    i = 0
    length = len(text)
    
    start_idx = 0
    
    while i < length:
        # Scan for opening brace
        if text[i] == '{':
            open_brace_idx = i
            # Selector is text[start_idx : open_brace_idx]
            raw_selector = text[start_idx:open_brace_idx]
            
            # Walk to find matching closing brace
            brace_count = 1
            i += 1
            while i < length and brace_count > 0:
                if text[i] == '{':
                    brace_count += 1
                elif text[i] == '}':
                    brace_count -= 1
                i += 1
            
            close_brace_idx = i
            full_block = text[start_idx:close_brace_idx]
            
            # Clean selector for comparison key
            key = clean_selector(raw_selector)
            
            blocks.append((key, full_block))
            start_idx = i # Next block starts here
        else:
            i += 1
            
    return blocks

def main():
    print("Reading files...")
    if not os.path.exists(RESTORE_PATH):
        print(f"Error: {RESTORE_PATH} not found.")
        return

    with open(RESTORE_PATH, 'r', encoding='utf-8') as f:
        restore_content = f.read()
    
    with open(CURRENT_PATH, 'r', encoding='utf-8') as f:
        current_content = f.read()
        
    print("Parsing restore file...")
    restore_blocks = parse_blocks(restore_content)
    restore_keys = set(k for k, b in restore_blocks if k) # Filter empty keys
    
    print(f"Indexed {len(restore_keys)} selectors from restore file.")
    
    print("Parsing current file...")
    current_blocks = parse_blocks(current_content)
    
    new_blocks = []
    for key, block in current_blocks:
        if not key: continue 
        if key not in restore_keys:
            # Check if it's not effectively empty or just whitespace
            new_blocks.append(block)
            
    print(f"Found {len(new_blocks)} new blocks.")
    
    final_content = restore_content + "\n\n/* ============================================\n   MERGED NEW FEATURES\n   ============================================ */\n\n" + "\n".join(new_blocks)
    
    with open(OUTPUT_PATH, 'w', encoding='utf-8') as f:
        f.write(final_content)
        
    print("Merge complete.")

if __name__ == "__main__":
    main()
