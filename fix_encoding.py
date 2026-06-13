import os, re

root = r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv'

# Table de remplacement des sequences corrompues (latin1 mal decode en utf-8)
fixes = {
    'Ã©': '\u00e9', 'Ã¨': '\u00e8', 'Ãª': '\u00ea', 'Ã«': '\u00eb',
    'Ã ': '\u00e0', 'Ã¢': '\u00e2', 'Ã¹': '\u00f9', 'Ã»': '\u00fb',
    'Ã®': '\u00ee', 'Ã¯': '\u00ef', 'Ã´': '\u00f4', 'Å\x93': '\u0153',
    'Ã§': '\u00e7', 'Ã‰': '\u00c9', 'Ã€': '\u00c0', 'Ã‡': '\u00c7',
    'Ã\x89': '\u00c9', 'Ã\x80': '\u00c0',
}

total = 0
for dirpath, _, files in os.walk(root):
    for fname in files:
        if not fname.endswith('.kt'):
            continue
        path = os.path.join(dirpath, fname)
        with open(path, encoding='utf-8') as f:
            content = f.read()
        # Supprimer BOM
        original = content
        content = content.lstrip('\ufeff')
        # Corriger les sequences corrompues
        for bad, good in fixes.items():
            content = content.replace(bad, good)
        if content != original:
            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f'Fixed: {fname}')
            total += 1

print(f'\nTotal: {total} fichiers corriges')
