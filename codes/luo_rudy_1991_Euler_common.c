#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include <time.h>
#define __DATA_NUM 1024


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

    clock_t t1, t2;
    t1 = clock();
    int __i;
    double* xi;
    double* xo;
    double* x1;
    double* x2;
    double* k1;
    double* y1;
    double* z;
    double t;
    double d = 0.010000;

    xi = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
    xo = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
    x1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
    x2 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
    y1 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __DATA_NUM )  )  ) ;;
    k1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
    z = (double*)malloc (  ( sizeof( double ) * 18 )  ) ;;
    /*Initialize constant variables*/
    z[0] = 8314;            // R
    z[1] = 310;             // T
    z[2] = 96484.6;         // F
    z[3] = 1;               // C
    z[4] = 0.01;            // stim_start (ms) = 100
    z[5] = 1.0E11;          // stim_end (ms)
    z[6] = 1000;            // stim_period (ms)
    z[7] = 2;               // stim_duration (ms) = 2
    z[8] = -25.5;           // stim_amplitude
    z[9] = 23;              // g_Na
    z[10] = 140;            // Nao
    z[11] = 18;             // Nai
    z[12] = 0.01833;        // PR_NaK
    z[13] = 5.4;            // Ko
    z[14] = 145;            // Ki
    z[15] = 0.0183;         // g_Kp
    z[16] = -59.87;         // E_b
    z[17] = 0.03921;        // g_b
    /*Initialize differencial variables*/
    for(__i = 0; __i < __DATA_NUM; __i++ ){
        xi[ ( 0 * __DATA_NUM ) + __i ] = -83.853;          // V
        xi[ ( 1 * __DATA_NUM ) + __i ] = 0.00187018;       // m
        xi[ ( 2 * __DATA_NUM ) + __i ] = 0.9804713;        // h
        xi[ ( 3 * __DATA_NUM ) + __i ] = 0.98767124;       // j
        xi[ ( 4 * __DATA_NUM ) + __i ] = 0.00316354;       // d
        xi[ ( 5 * __DATA_NUM ) + __i ] = 0.99427859;       // f
        xi[ ( 6 * __DATA_NUM ) + __i ] = 0.16647703;       // X
        xi[ ( 7 * __DATA_NUM ) + __i ] = 0.0002;           // Cai
    }
    for(t = 0.000000; ( t <= 10.000000 ) ;t =  ( t + d ) ){

        for(__i = 0; ( __i < __DATA_NUM ) ;__i++){

            x1[ (  ( 0 * __DATA_NUM )  + __i ) ] = xi[ (  ( 0 * __DATA_NUM )  + __i ) ];
            x1[ (  ( 1 * __DATA_NUM )  + __i ) ] = xi[ (  ( 1 * __DATA_NUM )  + __i ) ];
            x1[ (  ( 2 * __DATA_NUM )  + __i ) ] = xi[ (  ( 2 * __DATA_NUM )  + __i ) ];
            x1[ (  ( 3 * __DATA_NUM )  + __i ) ] = xi[ (  ( 3 * __DATA_NUM )  + __i ) ];
            x1[ (  ( 4 * __DATA_NUM )  + __i ) ] = xi[ (  ( 4 * __DATA_NUM )  + __i ) ];
            x1[ (  ( 5 * __DATA_NUM )  + __i ) ] = xi[ (  ( 5 * __DATA_NUM )  + __i ) ];
            x1[ (  ( 6 * __DATA_NUM )  + __i ) ] = xi[ (  ( 6 * __DATA_NUM )  + __i ) ];
            x1[ (  ( 7 * __DATA_NUM )  + __i ) ] = xi[ (  ( 7 * __DATA_NUM )  + __i ) ];
            y1[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
            y1[ (  ( 1 * __DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
            y1[ (  ( 2 * __DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
            y1[ (  ( 3 * __DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  ) )  )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
            y1[ (  ( 4 * __DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x1[ (  ( 7 * __DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
            y1[ (  ( 5 * __DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
            y1[ (  ( 6 * __DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
            y1[ (  ( 7 * __DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
            y1[ (  ( 8 * __DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
            y1[ (  ( 9 * __DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
            y1[ (  ( 10 * __DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
            y1[ (  ( 11 * __DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
            y1[ (  ( 12 * __DATA_NUM )  + __i ) ] = y1[ (  ( 10 * __DATA_NUM )  + __i ) ];
            y1[ (  ( 13 * __DATA_NUM )  + __i ) ] =  ( z[17] *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - z[16] )  ) ;
            y1[ (  ( 14 * __DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
            y1[ (  ( 15 * __DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
            y1[ (  ( 16 * __DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
            y1[ (  ( 17 * __DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
            y1[ (  ( 18 * __DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
            y1[ (  ( 19 * __DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
            y1[ (  ( 20 * __DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)5.476 )  - y1[ (  ( 10 * __DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] -  ( y1[ (  ( 10 * __DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
            y1[ (  ( 21 * __DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
            y1[ (  ( 22 * __DATA_NUM )  + __i ) ] =  ( z[9] * pow( x1[ (  ( 1 * __DATA_NUM )  + __i ) ] , (double)3 ) * x1[ (  ( 2 * __DATA_NUM )  + __i ) ] * x1[ (  ( 3 * __DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - y1[ (  ( 14 * __DATA_NUM )  + __i ) ] )  ) ;
            y1[ (  ( 23 * __DATA_NUM )  + __i ) ] =  ( (double)0.09 * x1[ (  ( 4 * __DATA_NUM )  + __i ) ] * x1[ (  ( 5 * __DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - y1[ (  ( 4 * __DATA_NUM )  + __i ) ] )  ) ;
            y1[ (  ( 24 * __DATA_NUM )  + __i ) ] =  ( y1[ (  ( 7 * __DATA_NUM )  + __i ) ] * x1[ (  ( 6 * __DATA_NUM )  + __i ) ] * y1[ (  ( 9 * __DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - y1[ (  ( 18 * __DATA_NUM )  + __i ) ] )  ) ;
            y1[ (  ( 25 * __DATA_NUM )  + __i ) ] =  ( y1[ (  ( 11 * __DATA_NUM )  + __i ) ] /  ( y1[ (  ( 11 * __DATA_NUM )  + __i ) ] + y1[ (  ( 20 * __DATA_NUM )  + __i ) ] )  ) ;
            y1[ (  ( 26 * __DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x1[ (  ( 0 * __DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
            y1[ (  ( 27 * __DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
            y1[ (  ( 28 * __DATA_NUM )  + __i ) ] =  ( z[15] * y1[ (  ( 21 * __DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - y1[ (  ( 12 * __DATA_NUM )  + __i ) ] )  ) ;
            y1[ (  ( 29 * __DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
            y1[ (  ( 30 * __DATA_NUM )  + __i ) ] =  ( y1[ (  ( 27 * __DATA_NUM )  + __i ) ] * y1[ (  ( 25 * __DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __DATA_NUM )  + __i ) ] )  ) ;
            k1[ (  ( 0 * __DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y1[ (  ( 0 * __DATA_NUM )  + __i ) ] + y1[ (  ( 22 * __DATA_NUM )  + __i ) ] + y1[ (  ( 23 * __DATA_NUM )  + __i ) ] + y1[ (  ( 24 * __DATA_NUM )  + __i ) ] + y1[ (  ( 30 * __DATA_NUM )  + __i ) ] + y1[ (  ( 28 * __DATA_NUM )  + __i ) ] + y1[ (  ( 13 * __DATA_NUM )  + __i ) ] )  ) ;
            k1[ (  ( 1 * __DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 1 * __DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 1 * __DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 15 * __DATA_NUM )  + __i ) ] * x1[ (  ( 1 * __DATA_NUM )  + __i ) ] )  ) ;
            k1[ (  ( 2 * __DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 2 * __DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 2 * __DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 26 * __DATA_NUM )  + __i ) ] * x1[ (  ( 2 * __DATA_NUM )  + __i ) ] )  ) ;
            k1[ (  ( 3 * __DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 3 * __DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 3 * __DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 16 * __DATA_NUM )  + __i ) ] * x1[ (  ( 3 * __DATA_NUM )  + __i ) ] )  ) ;
            k1[ (  ( 4 * __DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 5 * __DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 4 * __DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 17 * __DATA_NUM )  + __i ) ] * x1[ (  ( 4 * __DATA_NUM )  + __i ) ] )  ) ;
            k1[ (  ( 5 * __DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 6 * __DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 5 * __DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 29 * __DATA_NUM )  + __i ) ] * x1[ (  ( 5 * __DATA_NUM )  + __i ) ] )  ) ;
            k1[ (  ( 6 * __DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 8 * __DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 6 * __DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 19 * __DATA_NUM )  + __i ) ] * x1[ (  ( 6 * __DATA_NUM )  + __i ) ] )  ) ;
            k1[ (  ( 7 * __DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y1[ (  ( 23 * __DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x1[ (  ( 7 * __DATA_NUM )  + __i ) ] )  )  ) ;
            x2[ (  ( 0 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __DATA_NUM )  + __i ) ] * d )  ) ;
            x2[ (  ( 1 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __DATA_NUM )  + __i ) ] * d )  ) ;
            x2[ (  ( 2 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 2 * __DATA_NUM )  + __i ) ] * d )  ) ;
            x2[ (  ( 3 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 3 * __DATA_NUM )  + __i ) ] * d )  ) ;
            x2[ (  ( 4 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 4 * __DATA_NUM )  + __i ) ] * d )  ) ;
            x2[ (  ( 5 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 5 * __DATA_NUM )  + __i ) ] * d )  ) ;
            x2[ (  ( 6 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 6 * __DATA_NUM )  + __i ) ] * d )  ) ;
            x2[ (  ( 7 * __DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __DATA_NUM )  + __i ) ] +  ( k1[ (  ( 7 * __DATA_NUM )  + __i ) ] * d )  ) ;
            xo[ (  ( 0 * __DATA_NUM )  + __i ) ] = x2[ (  ( 0 * __DATA_NUM )  + __i ) ];
            xo[ (  ( 1 * __DATA_NUM )  + __i ) ] = x2[ (  ( 1 * __DATA_NUM )  + __i ) ];
            xo[ (  ( 2 * __DATA_NUM )  + __i ) ] = x2[ (  ( 2 * __DATA_NUM )  + __i ) ];
            xo[ (  ( 3 * __DATA_NUM )  + __i ) ] = x2[ (  ( 3 * __DATA_NUM )  + __i ) ];
            xo[ (  ( 4 * __DATA_NUM )  + __i ) ] = x2[ (  ( 4 * __DATA_NUM )  + __i ) ];
            xo[ (  ( 5 * __DATA_NUM )  + __i ) ] = x2[ (  ( 5 * __DATA_NUM )  + __i ) ];
            xo[ (  ( 6 * __DATA_NUM )  + __i ) ] = x2[ (  ( 6 * __DATA_NUM )  + __i ) ];
            xo[ (  ( 7 * __DATA_NUM )  + __i ) ] = x2[ (  ( 7 * __DATA_NUM )  + __i ) ];
            xi[ (  ( 0 * __DATA_NUM )  + __i ) ] = xo[ (  ( 0 * __DATA_NUM )  + __i ) ];
            xi[ (  ( 1 * __DATA_NUM )  + __i ) ] = xo[ (  ( 1 * __DATA_NUM )  + __i ) ];
            xi[ (  ( 2 * __DATA_NUM )  + __i ) ] = xo[ (  ( 2 * __DATA_NUM )  + __i ) ];
            xi[ (  ( 3 * __DATA_NUM )  + __i ) ] = xo[ (  ( 3 * __DATA_NUM )  + __i ) ];
            xi[ (  ( 4 * __DATA_NUM )  + __i ) ] = xo[ (  ( 4 * __DATA_NUM )  + __i ) ];
            xi[ (  ( 5 * __DATA_NUM )  + __i ) ] = xo[ (  ( 5 * __DATA_NUM )  + __i ) ];
            xi[ (  ( 6 * __DATA_NUM )  + __i ) ] = xo[ (  ( 6 * __DATA_NUM )  + __i ) ];
            xi[ (  ( 7 * __DATA_NUM )  + __i ) ] = xo[ (  ( 7 * __DATA_NUM )  + __i ) ];

        }


    }

    free ( xi ) ;
    free ( xo ) ;
    free ( x1 ) ;
    free ( x2 ) ;
    free ( y1 ) ;
    free ( k1 ) ;
    free ( z ) ;
    t2 = clock();
    printf("%s time=%f(sec)\n", __FILE__, (double)(t2 - t1) / CLOCKS_PER_SEC);
}


