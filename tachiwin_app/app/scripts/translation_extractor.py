import xml.etree.ElementTree as ET
import json
import os

def extract_strings(xml_file_path, json_file_path):
    # Create assets directory if it doesn't exist
    assets_dir = os.path.dirname(json_file_path)
    if not os.path.exists(assets_dir):
        os.makedirs(assets_dir)

    # Load existing translations if file exists
    existing_translations = {}
    if os.path.exists(json_file_path):
        with open(json_file_path, 'r', encoding='utf-8') as f:
            existing_translations = json.load(f)

    # Parse XML file
    tree = ET.parse(xml_file_path)
    root = tree.getroot()

    # Extract translations
    new_translations = {}
    for string in root.findall('string'):
        name = string.get('name')
        value = string.text
        if value is not None:
            # Only add if not in existing translations
            if name not in existing_translations:
                new_translations[name] = f"*{value}"

    # Merge translations (existing ones take precedence)
    final_translations = {**new_translations, **existing_translations}

    # Write to JSON file
    with open(json_file_path, 'w', encoding='utf-8') as f:
        json.dump(final_translations, f, ensure_ascii=False, indent=2)

    # Print statistics
    print(f"Translation statistics:")
    print(f"Total translations: {len(final_translations)}")
    print(f"New translations added: {len(new_translations)}")
    print(f"Existing translations preserved: {len(existing_translations)}")

if __name__ == "__main__":
    import sys

    if len(sys.argv) != 3:
        print("Usage: python script.py <path_to_strings.xml> <path_to_translations.json>")
        sys.exit(1)

    xml_path = sys.argv[1]
    json_path = sys.argv[2]

    try:
        extract_strings(xml_path, json_path)
        print("Translation extraction completed successfully!")
    except Exception as e:
        print(f"Error during translation extraction: {str(e)}")
        sys.exit(1)