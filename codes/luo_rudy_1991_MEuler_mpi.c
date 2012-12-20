#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include"mpi.h"
#include <time.h>
#define __DATA_NUM 1024
#define MASTER 0


int main ( int argc , char** argv ) ;

int main ( int argc , char** argv ) {

    clock_t t1, t2;
    t1 = clock();
    int start;
    int end;
    int __i;
    int __j;
    double* xi;
    double* xo;
    double* x1;
    double* x2;
    double* x3;
    double* k1;
    double* k2;
    double* y1;
    double* y2;
    double* z;
    double t;
    double d = 0.010000;
    double* all_xo;
    double end_time = 400.000000;
    int comm_size;
    int node;
    int tag = 0;
    int worker;
    MPI_Status recv_status;
    int __MASTER_DATA_NUM;
    int __WORKER_DATA_NUM;

    MPI_Init ( &argc , &argv ) ;
    MPI_Comm_size ( MPI_COMM_WORLD , &comm_size ) ;
    MPI_Comm_rank ( MPI_COMM_WORLD , &node ) ;
    if( ( comm_size < 3 ) ){

        __MASTER_DATA_NUM =  ( __DATA_NUM / comm_size ) ;
        __WORKER_DATA_NUM =  ( __DATA_NUM - __MASTER_DATA_NUM ) ;

    }

    else{

        __WORKER_DATA_NUM =  ( __DATA_NUM / comm_size ) ;
        __MASTER_DATA_NUM =  ( __DATA_NUM -  ( __WORKER_DATA_NUM *  ( comm_size - 1 )  )  ) ;

    }

    start =  (  ( __WORKER_DATA_NUM *  ( node - 1 )  )  + __MASTER_DATA_NUM ) ;
    end =  ( start + __WORKER_DATA_NUM ) ;
    if( ( node == 0 ) ){

        xi = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
        xo = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
        x1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
        x2 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
        x3 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
        all_xo = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
        y1 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __MASTER_DATA_NUM )  )  ) ;;
        y2 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __MASTER_DATA_NUM )  )  ) ;;
        k1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
        k2 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
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
        for(__j = 0; __j < __MASTER_DATA_NUM; __j++ ){
            xi[ ( 0 * __MASTER_DATA_NUM ) + __j ] = -83.853;          // V
            xi[ ( 1 * __MASTER_DATA_NUM ) + __j ] = 0.00187018;       // m
            xi[ ( 2 * __MASTER_DATA_NUM ) + __j ] = 0.9804713;        // h
            xi[ ( 3 * __MASTER_DATA_NUM ) + __j ] = 0.98767124;       // j
            xi[ ( 4 * __MASTER_DATA_NUM ) + __j ] = 0.00316354;       // d
            xi[ ( 5 * __MASTER_DATA_NUM ) + __j ] = 0.99427859;       // f
            xi[ ( 6 * __MASTER_DATA_NUM ) + __j ] = 0.16647703;       // X
            xi[ ( 7 * __MASTER_DATA_NUM ) + __j ] = 0.0002;           // Cai

        }
        for(t = 0.000000; ( t <= 10.000000 ) ;t =  ( t + d ) ){

            for(__i = 0; ( __i < __MASTER_DATA_NUM ) ;__i++){

                x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] = xi[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ];
                x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] = xi[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ];
                x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] = xi[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ];
                x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] = xi[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ];
                x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] = xi[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ];
                x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] = xi[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ];
                x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] = xi[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ];
                x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] = xi[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ];
                y1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
                y1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
                y1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
                y1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
                y1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
                y1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
                y1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
                y1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
                y1[ (  ( 8 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
                y1[ (  ( 9 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
                y1[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
                y1[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
                y1[ (  ( 12 * __MASTER_DATA_NUM )  + __i ) ] = y1[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ];
                y1[ (  ( 13 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[17] *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - z[16] )  ) ;
                y1[ (  ( 14 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
                y1[ (  ( 15 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
                y1[ (  ( 16 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
                y1[ (  ( 17 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
                y1[ (  ( 18 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
                y1[ (  ( 19 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
                y1[ (  ( 20 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)5.476 )  - y1[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] -  ( y1[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
                y1[ (  ( 21 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
                y1[ (  ( 22 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[9] * pow( x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] , (double)3 ) * x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y1[ (  ( 14 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.09 * x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 24 * __MASTER_DATA_NUM )  + __i ) ] =  ( y1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] * y1[ (  ( 9 * __MASTER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y1[ (  ( 18 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 25 * __MASTER_DATA_NUM )  + __i ) ] =  ( y1[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] /  ( y1[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] + y1[ (  ( 20 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 26 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
                y1[ (  ( 27 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
                y1[ (  ( 28 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[15] * y1[ (  ( 21 * __MASTER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y1[ (  ( 12 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 29 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
                y1[ (  ( 30 * __MASTER_DATA_NUM )  + __i ) ] =  ( y1[ (  ( 27 * __MASTER_DATA_NUM )  + __i ) ] * y1[ (  ( 25 * __MASTER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + y1[ (  ( 22 * __MASTER_DATA_NUM )  + __i ) ] + y1[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] + y1[ (  ( 24 * __MASTER_DATA_NUM )  + __i ) ] + y1[ (  ( 30 * __MASTER_DATA_NUM )  + __i ) ] + y1[ (  ( 28 * __MASTER_DATA_NUM )  + __i ) ] + y1[ (  ( 13 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 15 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 26 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 16 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 17 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 29 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 8 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 19 * __MASTER_DATA_NUM )  + __i ) ] * x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y1[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
                y2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
                y2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
                y2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
                y2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
                y2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
                y2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
                y2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
                y2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
                y2[ (  ( 8 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
                y2[ (  ( 9 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
                y2[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
                y2[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y2[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
                y2[ (  ( 12 * __MASTER_DATA_NUM )  + __i ) ] = y2[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ];
                y2[ (  ( 13 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[17] *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - z[16] )  ) ;
                y2[ (  ( 14 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
                y2[ (  ( 15 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
                y2[ (  ( 16 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
                y2[ (  ( 17 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
                y2[ (  ( 18 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
                y2[ (  ( 19 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
                y2[ (  ( 20 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)5.476 )  - y2[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] -  ( y2[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y2[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
                y2[ (  ( 21 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
                y2[ (  ( 22 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[9] * pow( x2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] , (double)3 ) * x2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y2[ (  ( 14 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.09 * x2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 24 * __MASTER_DATA_NUM )  + __i ) ] =  ( y2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] * y2[ (  ( 9 * __MASTER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y2[ (  ( 18 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 25 * __MASTER_DATA_NUM )  + __i ) ] =  ( y2[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] /  ( y2[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] + y2[ (  ( 20 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 26 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
                y2[ (  ( 27 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
                y2[ (  ( 28 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[15] * y2[ (  ( 21 * __MASTER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y2[ (  ( 12 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 29 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
                y2[ (  ( 30 * __MASTER_DATA_NUM )  + __i ) ] =  ( y2[ (  ( 27 * __MASTER_DATA_NUM )  + __i ) ] * y2[ (  ( 25 * __MASTER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y2[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + y2[ (  ( 22 * __MASTER_DATA_NUM )  + __i ) ] + y2[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] + y2[ (  ( 24 * __MASTER_DATA_NUM )  + __i ) ] + y2[ (  ( 30 * __MASTER_DATA_NUM )  + __i ) ] + y2[ (  ( 28 * __MASTER_DATA_NUM )  + __i ) ] + y2[ (  ( 13 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 15 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 26 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 16 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 17 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 29 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 8 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 19 * __MASTER_DATA_NUM )  + __i ) ] * x2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y2[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + k2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] + k2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] + k2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] + k2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] + k2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] + k2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] + k2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] + k2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
                xo[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] = x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ];
                xo[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] = x3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ];
                xo[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] = x3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ];
                xo[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] = x3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ];
                xo[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] = x3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ];
                xo[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] = x3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ];
                xo[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] = x3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ];
                xo[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] = x3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ];
                xi[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] = xo[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ];
                xi[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] = xo[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ];
                xi[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] = xo[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ];
                xi[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] = xo[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ];
                xi[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] = xo[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ];
                xi[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] = xo[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ];
                xi[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] = xo[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ];
                xi[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] = xo[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ];
                all_xo[ (  ( 0 * __DATA_NUM )  + __i ) ] = xo[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ];
                all_xo[ (  ( 1 * __DATA_NUM )  + __i ) ] = xo[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ];
                all_xo[ (  ( 2 * __DATA_NUM )  + __i ) ] = xo[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ];
                all_xo[ (  ( 3 * __DATA_NUM )  + __i ) ] = xo[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ];
                all_xo[ (  ( 4 * __DATA_NUM )  + __i ) ] = xo[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ];
                all_xo[ (  ( 5 * __DATA_NUM )  + __i ) ] = xo[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ];
                all_xo[ (  ( 6 * __DATA_NUM )  + __i ) ] = xo[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ];
                all_xo[ (  ( 7 * __DATA_NUM )  + __i ) ] = xo[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ];

            }

            for(worker = 1; ( worker < comm_size ) ;worker++){

                MPI_Recv ( &all_xo[ (  (  ( 0 * __DATA_NUM )  +  (  ( worker - 1 )  * __WORKER_DATA_NUM )  )  + __MASTER_DATA_NUM ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;
                MPI_Recv ( &all_xo[ (  (  ( 1 * __DATA_NUM )  +  (  ( worker - 1 )  * __WORKER_DATA_NUM )  )  + __MASTER_DATA_NUM ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;
                MPI_Recv ( &all_xo[ (  (  ( 2 * __DATA_NUM )  +  (  ( worker - 1 )  * __WORKER_DATA_NUM )  )  + __MASTER_DATA_NUM ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;
                MPI_Recv ( &all_xo[ (  (  ( 3 * __DATA_NUM )  +  (  ( worker - 1 )  * __WORKER_DATA_NUM )  )  + __MASTER_DATA_NUM ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;
                MPI_Recv ( &all_xo[ (  (  ( 4 * __DATA_NUM )  +  (  ( worker - 1 )  * __WORKER_DATA_NUM )  )  + __MASTER_DATA_NUM ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;
                MPI_Recv ( &all_xo[ (  (  ( 5 * __DATA_NUM )  +  (  ( worker - 1 )  * __WORKER_DATA_NUM )  )  + __MASTER_DATA_NUM ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;
                MPI_Recv ( &all_xo[ (  (  ( 6 * __DATA_NUM )  +  (  ( worker - 1 )  * __WORKER_DATA_NUM )  )  + __MASTER_DATA_NUM ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;
                MPI_Recv ( &all_xo[ (  (  ( 7 * __DATA_NUM )  +  (  ( worker - 1 )  * __WORKER_DATA_NUM )  )  + __MASTER_DATA_NUM ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;

            }


        }

        free ( xi ) ;
        free ( xo ) ;
        free ( x1 ) ;
        free ( x2 ) ;
        free ( x3 ) ;
        free ( y1 ) ;
        free ( y2 ) ;
        free ( k1 ) ;
        free ( k2 ) ;
        free ( z ) ;
        free ( all_xo ) ;

    }

    else{

        xi = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
        xo = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
        x1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
        x2 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
        x3 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
        y1 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __DATA_NUM )  )  ) ;;
        y2 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __DATA_NUM )  )  ) ;;
        k1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
        k2 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
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
        for(__j = start; __j < end; __j++ ){
            xi[ ( 0 * __WORKER_DATA_NUM ) + __j ] = -83.853;          // V
            xi[ ( 1 * __WORKER_DATA_NUM ) + __j ] = 0.00187018;       // m
            xi[ ( 2 * __WORKER_DATA_NUM ) + __j ] = 0.9804713;        // h
            xi[ ( 3 * __WORKER_DATA_NUM ) + __j ] = 0.98767124;       // j
            xi[ ( 4 * __WORKER_DATA_NUM ) + __j ] = 0.00316354;       // d
            xi[ ( 5 * __WORKER_DATA_NUM ) + __j ] = 0.99427859;       // f
            xi[ ( 6 * __WORKER_DATA_NUM ) + __j ] = 0.16647703;       // X
            xi[ ( 7 * __WORKER_DATA_NUM ) + __j ] = 0.0002;           // Cai

        }
        for(t = 0.000000; ( t <= 10.000000 ) ;t =  ( t + d ) ){

            for(__i = start; ( __i < end ) ;__i++){

                x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] = xi[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ];
                x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] = xi[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ];
                x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] = xi[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ];
                x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] = xi[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ];
                x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] = xi[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ];
                x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] = xi[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ];
                x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] = xi[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ];
                x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] = xi[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ];
                y1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
                y1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
                y1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
                y1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
                y1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
                y1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
                y1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
                y1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
                y1[ (  ( 8 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
                y1[ (  ( 9 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
                y1[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
                y1[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
                y1[ (  ( 12 * __WORKER_DATA_NUM )  + __i ) ] = y1[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ];
                y1[ (  ( 13 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[17] *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - z[16] )  ) ;
                y1[ (  ( 14 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
                y1[ (  ( 15 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
                y1[ (  ( 16 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
                y1[ (  ( 17 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
                y1[ (  ( 18 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
                y1[ (  ( 19 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
                y1[ (  ( 20 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)5.476 )  - y1[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] -  ( y1[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
                y1[ (  ( 21 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
                y1[ (  ( 22 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[9] * pow( x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] , (double)3 ) * x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y1[ (  ( 14 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.09 * x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 24 * __WORKER_DATA_NUM )  + __i ) ] =  ( y1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] * y1[ (  ( 9 * __WORKER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y1[ (  ( 18 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 25 * __WORKER_DATA_NUM )  + __i ) ] =  ( y1[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] /  ( y1[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] + y1[ (  ( 20 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 26 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
                y1[ (  ( 27 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
                y1[ (  ( 28 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[15] * y1[ (  ( 21 * __WORKER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y1[ (  ( 12 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y1[ (  ( 29 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
                y1[ (  ( 30 * __WORKER_DATA_NUM )  + __i ) ] =  ( y1[ (  ( 27 * __WORKER_DATA_NUM )  + __i ) ] * y1[ (  ( 25 * __WORKER_DATA_NUM )  + __i ) ] *  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y1[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + y1[ (  ( 22 * __WORKER_DATA_NUM )  + __i ) ] + y1[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] + y1[ (  ( 24 * __WORKER_DATA_NUM )  + __i ) ] + y1[ (  ( 30 * __WORKER_DATA_NUM )  + __i ) ] + y1[ (  ( 28 * __WORKER_DATA_NUM )  + __i ) ] + y1[ (  ( 13 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 15 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 26 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 16 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 17 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 29 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y1[ (  ( 8 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y1[ (  ( 19 * __WORKER_DATA_NUM )  + __i ) ] * x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y1[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
                x2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
                y2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
                y2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
                y2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
                y2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
                y2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
                y2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
                y2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
                y2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
                y2[ (  ( 8 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
                y2[ (  ( 9 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
                y2[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
                y2[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y2[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
                y2[ (  ( 12 * __WORKER_DATA_NUM )  + __i ) ] = y2[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ];
                y2[ (  ( 13 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[17] *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - z[16] )  ) ;
                y2[ (  ( 14 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
                y2[ (  ( 15 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
                y2[ (  ( 16 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
                y2[ (  ( 17 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
                y2[ (  ( 18 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
                y2[ (  ( 19 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
                y2[ (  ( 20 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)5.476 )  - y2[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] -  ( y2[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y2[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
                y2[ (  ( 21 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
                y2[ (  ( 22 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[9] * pow( x2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] , (double)3 ) * x2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y2[ (  ( 14 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.09 * x2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 24 * __WORKER_DATA_NUM )  + __i ) ] =  ( y2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] * y2[ (  ( 9 * __WORKER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y2[ (  ( 18 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 25 * __WORKER_DATA_NUM )  + __i ) ] =  ( y2[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] /  ( y2[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] + y2[ (  ( 20 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 26 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
                y2[ (  ( 27 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
                y2[ (  ( 28 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[15] * y2[ (  ( 21 * __WORKER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y2[ (  ( 12 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                y2[ (  ( 29 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
                y2[ (  ( 30 * __WORKER_DATA_NUM )  + __i ) ] =  ( y2[ (  ( 27 * __WORKER_DATA_NUM )  + __i ) ] * y2[ (  ( 25 * __WORKER_DATA_NUM )  + __i ) ] *  ( x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y2[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + y2[ (  ( 22 * __WORKER_DATA_NUM )  + __i ) ] + y2[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] + y2[ (  ( 24 * __WORKER_DATA_NUM )  + __i ) ] + y2[ (  ( 30 * __WORKER_DATA_NUM )  + __i ) ] + y2[ (  ( 28 * __WORKER_DATA_NUM )  + __i ) ] + y2[ (  ( 13 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 15 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 26 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 16 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 17 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 29 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y2[ (  ( 8 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y2[ (  ( 19 * __WORKER_DATA_NUM )  + __i ) ] * x2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
                k2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y2[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + k2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] + k2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] + k2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] + k2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] + k2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] + k2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] + k2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                x3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)2 )  *  ( k1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] + k2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
                xo[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] = x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ];
                xo[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] = x3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ];
                xo[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] = x3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ];
                xo[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] = x3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ];
                xo[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] = x3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ];
                xo[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] = x3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ];
                xo[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] = x3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ];
                xo[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] = x3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ];
                xi[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ];
                xi[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ];
                xi[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ];
                xi[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ];
                xi[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ];
                xi[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ];
                xi[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ];
                xi[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ];

            }

            MPI_Send ( &xo[ (  ( 0 * __WORKER_DATA_NUM )  + start ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;
            MPI_Send ( &xo[ (  ( 1 * __WORKER_DATA_NUM )  + start ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;
            MPI_Send ( &xo[ (  ( 2 * __WORKER_DATA_NUM )  + start ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;
            MPI_Send ( &xo[ (  ( 3 * __WORKER_DATA_NUM )  + start ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;
            MPI_Send ( &xo[ (  ( 4 * __WORKER_DATA_NUM )  + start ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;
            MPI_Send ( &xo[ (  ( 5 * __WORKER_DATA_NUM )  + start ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;
            MPI_Send ( &xo[ (  ( 6 * __WORKER_DATA_NUM )  + start ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;
            MPI_Send ( &xo[ (  ( 7 * __WORKER_DATA_NUM )  + start ) ] , __WORKER_DATA_NUM , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;

        }

        free ( xi ) ;
        free ( xo ) ;
        free ( x1 ) ;
        free ( x2 ) ;
        free ( x3 ) ;
        free ( y1 ) ;
        free ( y2 ) ;
        free ( k1 ) ;
        free ( k2 ) ;
        free ( z ) ;

    }

    t2 = clock();
    printf("%s node=%d : time=%f(sec)\n", __FILE__, node, (double)(t2 - t1) / CLOCKS_PER_SEC);
    MPI_Finalize (  ) ;
}



