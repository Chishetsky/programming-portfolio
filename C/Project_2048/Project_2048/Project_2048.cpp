// Project_2048.cpp : Defines the entry point for the application.
//

#include "Project_2048.h"
#include "functions.h"
using namespace std;

int Matrix[MATRIX_SIZE + 1][MATRIX_SIZE + 1] = { 0 };
int score = 0, last = 0;
char  username[9];

int main(int argc, char** argv)
{
    char Direction;
    int temp;
    int arow;
    welcome_menu();
    menu();
    Direction = DOWN;


    while (Direction != 57)
    {
        Direction = _getch();
        arow = Direction;
        key_choice(arow);

        temp = temp_count();

        if (temp == 1)
        {
            printf("\t\tYOU HAVE WON!\n\n");
            endgame();
            break;
        }

        if (temp == 0)
        {
            system("cls");
            printf("\n\n\n\t\t\tGAME OVER!\n");
            endgame();
            break;
        }
    }

    return 0;
}

//UVODNE MENU 
void welcome_menu()
{
    system("color  A");
    printf("\n\n\n");
    printf("\t\t\tXXX        XXX    XXXXXXXXXXXX    XXX             XXX             XXXXXXXXXXXX    XXX \n");
    printf("\t\t\tXXX        XXX    XXXXXXXXXXXX    XXX             XXX             XXXXXXXXXXXX    XXX \n");
    printf("\t\t\tXXX        XXX    XXX             XXX             XXX             XXX      XXX    XXX \n");
    printf("\t\t\tXXX        XXX    XXX             XXX             XXX             XXX      XXX    XXX \n");
    printf("\t\t\tXXXXXXXXXXXXXX    XXXXXXXXXXXX    XXX             XXX             XXX      XXX    XXX \n");
    printf("\t\t\tXXXXXXXXXXXXXX    XXXXXXXXXXXX    XXX             XXX             XXX      XXX    XXX \n");
    printf("\t\t\tXXX        XXX    XXX             XXX             XXX             XXX      XXX    XXX \n");
    printf("\t\t\tXXX        XXX    XXXXXXXXXXXX    XXXXXXXXXXXX    XXXXXXXXXXXX    XXXXXXXXXXXX        \n");
    printf("\t\t\tXXX        XXX    XXXXXXXXXXXX    XXXXXXXXXXXX    XXXXXXXXXXXX    XXXXXXXXXXXX    XXX \n");

    printf("\t\t\t\n\n\n");

    printf("\n\n\t\t\tPRESS ANY KEY TO CONTINUE\n\n");
    while (!_kbhit());
}


void menu()
{
    int choice;

    system("cls");
    printf("\n\n\n\t\t\t\t\t\tWELCOME EARTHLING!\n\n\n\n");
    printf("\t\t\t\t\t\tWhat you wanna do?\n\n");
    printf("\t\t\t\t\t\t(1) START NEW GAME\n");
    printf("\t\t\t\t\t\t(2) RETURN TO GAME\n");
    printf("\t\t\t\t\t\t(3) VIEW STATS\n");
    printf("\t\t\t\t\t\t(4) EXIT\n");

    scanf("%d", &choice);

    initializer(choice);
}

//VOLBA OPERACIE
void initializer(int operation)
{
    switch (operation)
    {
    case 1:
        new_game();
        break;
    case 2:
        old_game();
        break;
    case 3:
        view_stats();
        break;
    case 4:
        exit(0);
        break;
    default:
        printf("\033[31m");
        printf("\n\nERROR!Wrong operation!\n");
        printf("\033[1;32m");
        exit(0);
        break;
    }
}

//NOVA HRA
void new_game()
{
    entering_name();
    matrix_intitialize();
}

//NAVRAT K ROZOHRATEJ ULOZENEJ HRE
void old_game()
{
    FILE* file;
    int a = 0;
    
    entering_name();
    file = fopen("old_game_score.txt", "r");
    fscanf(file, "%d", &a);
    if (!feof(file))
    {
        score = a;
    }

    fclose(file);
    int temp2 = 0;
    FILE* txt;
    txt = fopen("old_game.txt", "r");


    if (txt == NULL)
    {
        printf("\033[31m");
        printf("Error opening file with old game\n");
        printf("\033[1;32m");
        exit(0);
    }

    for (int i = 0; i < MATRIX_SIZE; i++)
    {
        for (int j = 0; j < MATRIX_SIZE; j++)
        {
            fscanf(txt, "%d", &temp2);
            Matrix[i][j] = temp2;
        }
    }
    fclose(txt);
    display_window();
}


void old_game_score()
{
    FILE* txt;
    txt = fopen("old_game_score.txt", "r");
    int a = 0;
    fscanf(txt, "%d", &a);
    if (!feof(txt))
    {
        score = a;
    }

    fclose(txt);
    old_game();

}   

//ZOBRAZIT STATISTIKY
void view_stats()
{

    FILE* txt;
    char game_score[100];
    int* scorelist;
    int count = 1;
    char* row;
    int i = 0;
    char* oldscore_str;
    int oldscore;
    int temp = 0;
    txt = fopen("score.txt", "r");

    if (txt == NULL)
    {
        printf("\033[31m");
        printf("Error opening file with stats\n");
        printf("\033[1;32m");
        return;
    }

    scorelist = (int*)malloc(2 * sizeof(int*));
    if (scorelist == NULL)
    {
        printf("ERROR! MEMORY ALLOCATION FAILED\n");
        exit(0);
    }


    fseek(txt, 0, SEEK_SET);
    printf("\t\t\tLEADERBOARD\n");
    while (fgets(game_score, 100, txt))
    {
        row = strtok(game_score, "\t");

        while (row != NULL)
        {
            row = strtok(NULL, "\t");
            oldscore_str = (char*)malloc(2 * sizeof(row));
            if (oldscore_str == NULL)
            {
                printf("ERROR! MEMORY ALLOCATION FAILED\n");
                exit(0);
            }
            if (row != NULL)
            {

                strcpy(oldscore_str, row);
                oldscore = atoi(oldscore_str);



                scorelist[i] = oldscore;
                i++;

            }

        }

    }

    fclose(txt);
    txt = fopen("score.txt", "r");

    for (int i = 0; i < 11; i++)
    {
        for (int j = i + 1; j < 11; j++)
        {
            if (scorelist[i] < scorelist[j])
            {
                temp = scorelist[i];
                scorelist[i] = scorelist[j];
                scorelist[j] = temp;
            }
        }
    }
    for (int i = 0; i < scorelist[i]; i++)
    {
        if (scorelist[i] > 0 && scorelist[i] != NULL && scorelist[i] <  9000)
        {
            if (count <= 10)
            {
                printf("\t\t#%d %d\n", count, scorelist[i]);
                count++;
            }

        }      
    }
    fclose(txt);
    printf("\n\n\n\n");
    exit(0);
}

//VYTVORENIE MATICE A PRIRADENIE POCIATOCNYCH HODNOT
void matrix_intitialize()
{
    int a, b, c, d;
    time_t t;
    
    srand(time(&t));
    a = (rand() % 4);
    b = (rand() % 4);
    c = (rand() % 4);
    d = (rand() % 4);

    if ((a == b == c == d) || (a==c && b==d))
    {
        a = 0;
        b = 1;
        c = 2;
    }


    Matrix[a][b] = 2;
    Matrix[c][d] = 2;
    display_window();
}

//ZOBRAZOVACIE OKNO
void display_window()
{
    system("cls");
    printf("\n\n\n\t\t\tGAME CONTROL BY ARROW KEYS OR W,A,S,D KEYS\n");
    printf("\n\n\n\t\t\tTO QUIT THE GAME PRESS SPACE\n");
    printf("\t\t\t\t\t\t\t\t SCORE  : %d\n\n\n\n", score);
    printf("\t\t\t -_-_-_-_-_-_-_-_-_-_-_-_-_\n");

    for (int i = 0; i < MATRIX_SIZE; i++)
    {
        printf("\t\t\t =");
        for (int j = 0; j < MATRIX_SIZE; j++)
        {
            if (Matrix[i][j] == 0)
            {
                printf("      ");
            }
            else
            {
                printf(" %4d ", Matrix[i][j]);

            }
        }
        printf("=\n\t\t\t =                        =\n");

    }

    printf("\t\t\t -_-_-_-_-_-_-_-_-_-_-_-_-_\n\n");
}

//VYBER SIPKY
void key_choice(int Direction)
{
    switch (Direction)
    {
    case UP:
    {
        up_direction();
        break;
    }

    case DOWN:
    {
        down_direction();
        break;
    }

    case LEFT:
    {
        left_direction();
        break;
    }

    case RIGHT:
    {
        right_direction();
        break;
    }
    case SPACE:
    {
        printf("ENDING GAME, SAVING DATA....\n");
        save_game();
        save_game_score();
        endgame();
        exit(0);
        break;
    }
    
    case W_key:
    {
        up_direction();
        break;
    }

    case S_key:
    {
        down_direction();
        break;
    }

    case A_key:
    {
        left_direction();
        break;
    }

    case D_key:
    {
        right_direction();
        break;
    }
    case w_key:
    {
        up_direction();
        break;
    }

    case s_key:
    {
        down_direction();
        break;
    }

    case a_key:
    {
        left_direction();
        break;
    }

    case d_key:
    {
        right_direction();
        break;
    }


    default:
    {
        return;
    }
    }
    Random_creater();
    display_window();

}

//PRIDAVANIE NOVYCH POLI
void Random_creater()
{

    int temp1, temp2, add, i, j;
    srand(time(NULL));
    temp1 = rand() % MATRIX_SIZE;

    srand(time(NULL));
    temp2 = rand() % MATRIX_SIZE;

    if ((temp1 + temp2) % 2 == 0)
    {
        add = 2;
    }

    else
    {
        add = 4;
    }

    for (i = 0; i < temp1; i++)
    {
        for (j = temp2; j < MATRIX_SIZE; j++)
        {
            if (Matrix[i][j] == 0)
            {
                Matrix[i][j] = add;
                return;
            }
        }
    }

}

//LOGIKA DOLNEJ SMEROVEJ SIPKY
void down_direction()
{
    int i;
    for (int j = 0; j < MATRIX_SIZE; j++)
    {
        i = 2;
        while (1)
        {
            while (Matrix[i][j] == 0)
            {
                if (i == -1)
                {
                    break;

                }
                i--;
            }
            if (i == -1)
            {
                break;
            }
            while (i < MATRIX_SIZE - 1 && (Matrix[i + 1][j] == 0 || Matrix[i][j] == Matrix[i + 1][j]))
            {
                if (Matrix[i][j] == Matrix[i + 1][j])
                {
                    score += (Matrix[i][j] + Matrix[i + 1][j]);
                }
                Matrix[i + 1][j] += Matrix[i][j];
                Matrix[i][j] = 0;
                i++;

            }
            i--;
        }
    }
}

//LOGIKA HORNEJ SMEROVEJ SIPKY
void up_direction()
{
    int i;
    for (int j = 0; j < MATRIX_SIZE; j++)
    {
        i = 1;
        while (1)
        {
            while (Matrix[i][j] == 0)
            {
                if (i == MATRIX_SIZE)
                {
                    break;

                }
                i++;

            }
            if (i == MATRIX_SIZE)
            {
                break;
            }
            while (i > 0 && (Matrix[i - 1][j] == 0 || Matrix[i][j] == Matrix[i - 1][j]))
            {
                if (Matrix[i][j] == Matrix[i - 1][j])
                {
                    score += (Matrix[i][j] + Matrix[i - 1][j]);
                }
                Matrix[i - 1][j] += Matrix[i][j];
                Matrix[i][j] = 0;
                i--;

            }
            i++;
        }
    }
}
//LOGIKA LAVEJ SMEROVEJ SIPKY
void left_direction()
{
    int j;
    for (int i = 0; i < MATRIX_SIZE; i++)
    {
        j = 1;
        while (1)
        {
            while (Matrix[i][j] == 0)
            {
                if (j == MATRIX_SIZE)
                {
                    break;

                }
                j++;
            }
            if (j == MATRIX_SIZE)
            {
                break;
            }

            while (j > 0 && (Matrix[i][j - 1] == 0 || Matrix[i][j] == Matrix[i][j - 1]))
            {
                if (Matrix[i][j] == Matrix[i][j - 1])
                {
                    score += (Matrix[i][j] + Matrix[i][j - 1]);
                }
                Matrix[i][j - 1] += Matrix[i][j];
                Matrix[i][j] = 0;
                j--;

            }
            j++;
        }
    }
}

//LOGIKA PRAVEJ SMEROVEJ SIPKY
void right_direction()
{
    int j;
    for (int i = 0; i < MATRIX_SIZE; i++)
    {
        j = 2;
        while (1)
        {
            while (Matrix[i][j] == 0)
            {
                if (j == -1)
                {
                    break;
                }
                j--;
            }
            if (j == -1)
            {
                break;
            }
            while (j < MATRIX_SIZE - 1 && (Matrix[i][j + 1] == 0 || Matrix[i][j] == Matrix[i][j + 1]))
            {
                if (Matrix[i][j] == Matrix[i][j + 1])
                {
                    score += (Matrix[i][j] + Matrix[i][j + 1]);
                }
                Matrix[i][j + 1] += Matrix[i][j];
                Matrix[i][j] = 0;
                j++;

            }
            j--;
        }
    }
}


int temp_count()
{
    int temp = 0;

    for (int i = 0; i < MATRIX_SIZE; i++)
    {
        for (int j = 0; j < MATRIX_SIZE; j++)
        {
            if (Matrix[i][j] == 2048)
            {
                return 1;
            }

            if (Matrix[i][j] == 0)
            {
                temp = 1;
            }
        }
    }

    if (temp == 1)
    {
        last = 0;
        return -99;
    }

    else
    {
        if (last == 1)
        {
            return 0;
        }
        last = 1;
        return -99;
    }
}

//ULOZENIE POSLEDNEJ HRY
void save_game()
{
    FILE* txt;
    int temp;

    txt = fopen("old_game.txt", "w");

    if (txt == NULL)
    {
        printf("Error opening file with stats\n");
        return;
    }

    for (int i = 0; i < MATRIX_SIZE; i++)
    {
        for (int j = 0; j < MATRIX_SIZE; j++)
        {
            temp = Matrix[i][j];
            fprintf(txt, "%d\n", temp);
        }
    }
    fclose(txt);

}

void save_game_score()
{
    FILE* txt;

    txt = fopen("old_game_score.txt", "w");

    if (txt == NULL)
    {
        printf("Error opening file with stats\n");
        return;
    }

    fprintf(txt, "%d\n", score);

    fclose(txt);
}

//ULOZENIE CELKOVEHO SKORE
void endgame()
{
    FILE* txt;
    char  str_scr[10];
    txt = fopen("score.txt", "a");

    if (txt == NULL)
    {
        printf("Error opening file with stats\n");
        return;
    }

    sprintf(str_scr, "%d\n", score);
    fseek(txt, 13, SEEK_SET);
    fputs(str_scr, txt);
    fputs("\n", txt);
    fclose(txt);
}
//TREBA ZADAT MEN PRED KAZDOU HROU
void entering_name()
{
    FILE* txt;
    int nick_length = 0;
    txt = fopen("score.txt", "a");

    if (txt == NULL)
    {
        printf("Error opening file with stats\n");
        return;
    }
    printf("Enter nick (max 8 characters): ");
    scanf("%s", &username);

    //KONTROLA DLZKY NICKU
    for (int i = 0; i < username[i]; i++)
    {
        nick_length++;
    }

    if (nick_length > 8)
    {
        printf("ERROR! Your nick is too long\n");
        exit(0);
    }

    fputs(username, txt);
    fputs("\t\t", txt);
    fclose(txt);
}