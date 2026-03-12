# Huffman Compression

Implementacja algorytmu kompresji Huffmana w języku Java z obsługą bloków o zmiennym rozmiarze (1-4 bajty).

## Opis

Program implementuje bezstratną kompresję danych przy użyciu algorytmu Huffmana. Główne cechy:

- **Kompresja bajt-po-bajcie** (domyślnie) - optymalna dla plików tekstowych
- **Kompresja blokowa** (1-4 bajty) - możliwość grupowania bajtów w większe symbole
- **Streaming** - obsługa dużych plików bez ładowania całości do pamięci
- **Własne struktury danych** - HashMap i PriorityQueue zaimplementowane od podstaw

## Wymagania

- Java 21 lub nowsza

## Użycie

### Kompresja

```bash
java -jar huffman.jar -c -i <plik_wejściowy> [-o <plik_wyjściowy>] [-l <rozmiar_bloku>]
```

### Dekompresja

```bash
java -jar huffman.jar -d -i <plik_skompresowany> [-o <plik_wyjściowy>]
```

### Opcje

| Opcja | Długa forma | Opis |
|-------|-------------|------|
| `-c` | `--compress` | Tryb kompresji |
| `-d` | `--decompress` | Tryb dekompresji |
| `-i` | `--input` | Plik wejściowy (wymagany) |
| `-o` | `--output` | Plik wyjściowy (opcjonalny) |
| `-l` | - | Rozmiar bloku 1-4 bajty (domyślnie: 1) |
| `-h` | `--help` | Wyświetla pomoc |

### Domyślne nazwy plików wyjściowych

- Kompresja: `<nazwa_pliku>.huff`
- Dekompresja: `<nazwa_pliku>.decoded`

## Format pliku skompresowanego

```
┌─────────────┬──────────────────┬─────────────────┬──────────────────┐
│ Group Size  │ Original Size    │ Huffman Tree    │ Encoded Data     │
│ (3 bity)    │ (32 bity)        │ (zmienna)       │ (zmienna)        │
└─────────────┴──────────────────┴─────────────────┴──────────────────┘
```

### Nagłówek
- **Group Size** (3 bity): Rozmiar bloku (1-4)
- **Original Size** (32 bity): Oryginalny rozmiar pliku w bajtach

### Drzewo Huffmana (serializacja preorder)
- `1` + symbol (groupSize × 8 bitów) - liść
- `0` - węzeł wewnętrzny (rekurencyjnie lewe i prawe poddrzewo)

### Dane
- Zakodowane symbole według wygenerowanych kodów Huffmana
- Dopełnienie zerami do pełnego bajta na końcu

## Algorytm

### Kompresja

1. **Zliczanie częstości** - jednoprzebiegowe zliczanie symboli (bajtów lub bloków)
2. **Budowanie drzewa** - algorytm Huffmana z użyciem kolejki priorytetowej
3. **Generowanie kodów** - przejście drzewa, przypisanie kodów (0 = lewo, 1 = prawo)
4. **Zapis nagłówka** - rozmiar bloku + oryginalny rozmiar pliku
5. **Serializacja drzewa** - zapis struktury drzewa
6. **Kodowanie danych** - zamiana symboli na kody Huffmana

### Dekompresja

1. **Odczyt nagłówka** - rozmiar bloku i oryginalny rozmiar
2. **Rekonstrukcja drzewa** - deserializacja z formatu preorder
3. **Dekodowanie danych** - nawigacja po drzewie bit-po-bicie
4. **Zapis bajtów** - konwersja symboli z powrotem na bajty

### Obsługa przypadków specjalnych

- **Pusty plik** - zapisywany tylko nagłówek z rozmiarem 0
- **Pojedynczy symbol** - kod o długości 1 bit (zamiast 0)
- **Niepełne bloki** - ostatni blok dopełniany zerami z prawej strony

## Przykłady

### Podstawowa kompresja

```bash
# Kompresja pliku tekstowego
java -jar huffman.jar -c -i dokument.txt

# Wynik: dokument.txt.huff
```

### Kompresja z blokami 2-bajtowymi

```bash
# Lepsze wyniki dla plików z powtarzającymi się parami bajtów
java -jar huffman.jar -c -i data.bin -l 2 -o data.compressed
```

### Dekompresja

```bash
# Dekompresja do oryginalnej postaci
java -jar huffman.jar -d -i dokument.txt.huff -o dokument_restored.txt
```

### Porównanie efektywności

```bash
# Plik tekstowy (częste litery jak 'e', 'a', spacja)
java -jar huffman.jar -c -i book.txt
# Typowa kompresja: 50-60% oryginalnego rozmiaru

# Plik binarny (równomierny rozkład bajtów)
java -jar huffman.jar -c -i image.jpg
# Typowa kompresja: ~100% (brak zysku - już skompresowany)
```

## Wydajność

### Złożoność czasowa

| Operacja | Złożoność |
|----------|-----------|
| Kompresja | O(n + k log k) |
| Dekompresja | O(n × h) |

Gdzie: n = rozmiar pliku, k = liczba unikalnych symboli, h = wysokość drzewa

### Optymalizacje

- **Bulk read/write** - odczyt/zapis wielu bitów naraz zamiast bit-po-bicie
- **Buforowanie** - 8KB bufory dla I/O
- **Reużycie buforów** - minimalizacja alokacji w pętlach

## Autor

Arkadiusz Perko (337603)
