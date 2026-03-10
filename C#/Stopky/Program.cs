// See https://aka.ms/new-console-template for more information



using System.IO;
using System.Threading;
using System.Text.Json;

//Telo metody main (kompilator doplnuje automaticky)
vypisMenu();
Stopky noveStopky = new Stopky();
string cestaKsuboru = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "Stopky", "okruhy.json");
List<Okruh> okruhy = nacitajOkruh(cestaKsuboru);
int cas = Console.CursorTop;
spustiVykonavanie();


void vypisMenu()
{
    Console.WriteLine("\n*********************");
    Console.WriteLine("VITAJTE!\n");
    Console.WriteLine("Toto je aplikacia \"Stopky\".");
    Console.WriteLine("Vyberte jednu z nasledovnych moznosti:\n");
    Console.WriteLine("(1) Start");
    Console.WriteLine("(2) Stop");
    Console.WriteLine("(3) Reset");
    Console.WriteLine("(4) Ukoncit");
    Console.WriteLine("(5) Ulozit okruh");
    Console.WriteLine("(6) Zobrazit zoznam okruhov");
    Console.WriteLine("\n*********************");
}

void spustiVykonavanie()
{
    Console.CursorVisible = false;
    while (true)
    {
        //Nastavenie formatu a dlzky retazca, prepisuje sa cyklicky len jeden riadok novymi hodnotami
        Console.SetCursorPosition(0, cas);
        string t = noveStopky.uplynulyCas.ToString(@"hh\:mm\:ss\.fff");
        Console.Write(t.PadRight(12));

        //Kontrola uzivatelskeho vstupu
        if (Console.KeyAvailable)
        {
            var vyber = Console.ReadKey(true).Key;
            switch (vyber)
            {
                case ConsoleKey.NumPad1:
                case ConsoleKey.D1:

                    if (!noveStopky.spustene)
                    {
                        startStopky(noveStopky);
                    }
                    break;
                case ConsoleKey.NumPad2:
                case ConsoleKey.D2:
                    if (noveStopky.spustene)
                    {
                        stopStopky(noveStopky);
                    }
                    break;

                case ConsoleKey.NumPad3:
                case ConsoleKey.D3:
                    resetStopky(noveStopky);
                    break;
                case ConsoleKey.NumPad4:
                case ConsoleKey.D4:
                    ulozOkruh(cestaKsuboru, okruhy);
                    Console.CursorVisible = true;
                    Console.WriteLine("\nUkoncujem program...");
                    return;
                case ConsoleKey.NumPad5:
                case ConsoleKey.D5:
                    pridajOkruh();
                    break;
                case ConsoleKey.NumPad6:
                case ConsoleKey.D6:
                    zobrazOkruhy();
                    break;
                default:
                    Console.WriteLine("\nCHYBA: Nespravna volba");
                    break;
            }
        }

        Thread.Sleep(80);

    }
}
    //Metody pre jednotlive zvolene akcie 

    void startStopky(Stopky stopky)
    {
        stopky.Start();
    }

    void stopStopky(Stopky stopky) 
    {
        stopky.Stop();
    }

    void resetStopky(Stopky stopky) 
    {
        stopky.Reset();
        
    }

void pridajOkruh()
{
    Console.SetCursorPosition(0, cas + 2);
    Console.Write("\nNazov okruhu: ");
    Console.CursorVisible = true;
    var nazov = Console.ReadLine();
    Console.CursorVisible = false;

    if (string.IsNullOrWhiteSpace(nazov))
    {
        nazov = $"Okruh {okruhy.Count + 1}";
    }

    var snapshot = noveStopky.uplynulyCas;
    okruhy.Add(new Okruh { Nazov = nazov.Trim(), Cas = snapshot });
    ulozOkruh(cestaKsuboru, okruhy);

    Console.WriteLine($"Ulozene: {nazov} - {snapshot:hh\\:mm\\:ss\\.fff}");
    Console.SetCursorPosition(0, cas);
}

void zobrazOkruhy()
{
    Console.WriteLine();
    if (okruhy.Count == 0)
    {
        Console.WriteLine("Ziadne ulozene okruhy");
        return;

    }
    for (int i = 0; i < okruhy.Count; i++)
    {
        Console.WriteLine($"{i + 1}. {okruhy[i].Nazov}: {okruhy[i].Cas:hh\\:mm\\:ss\\.fff}");

    }
}

static List<Okruh> nacitajOkruh(string cesta)
{
    try
    {
        if (!File.Exists(cesta))
        {
            return new();
        }
        var json = File.ReadAllText(cesta);
        return JsonSerializer.Deserialize<List<Okruh>>(json) ?? new();
    }
    catch
    {

        return new();
    }
}

static void ulozOkruh(string cesta, List<Okruh> data)
{
    var dir = Path.GetDirectoryName(cesta);
    if (!string.IsNullOrEmpty(dir))
    {
        Directory.CreateDirectory(dir!);
    }
    var json = JsonSerializer.Serialize(data, new JsonSerializerOptions { WriteIndented = true });
    File.WriteAllText(cesta, json);
}













