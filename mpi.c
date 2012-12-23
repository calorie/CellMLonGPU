#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include"mpi.h"
#define __DATA_NUM 32
#define MASTER 0
#include<string.h>
#include<search.h>
#include<sys/stat.h>
#define BUFF_MAX 100
#define TEST_MAX 20

FILE* fp;
ENTRY e;
ENTRY* ep;
char op;

void testInit ( char** argv , int node ) ;
void testCell ( char* testName , double cell , double diff ) ;
int main ( int argc , char** argv ) ;

void testInit ( char** argv , int node ) {

	char* fileName;

	fileName = (char*)malloc (  ( sizeof( char ) *  ( strlen ( "data/node.dat" ) + sizeof( char ) )  )  );
	if( ( argv[1] != NULL ) ){

		*argv++;
		if( ( **argv == '-' ) ){

			op = * ( *argv + 1 ) ;
			if( ( op == 't' ) ){

				sprintf ( fileName , "data/node%d.dat" , node ) ;
				fp = fopen ( fileName , "r" );

			}

			else if( ( op == 'w' ) ){

				mkdir ( "./data" , 0777 ) ;
				sprintf ( fileName , "data/node%d.dat" , node ) ;
				fp = fopen ( fileName , "w" );

			}

			else{

				exit ( EXIT_FAILURE ) ;

			}


		}


	}

	free ( fileName ) ;
}


void testCell ( char* testName , double cell , double diff ) {

	char* tag;
	char buff[BUFF_MAX];
	int cnt = 0;
	double f_cell;

	tag = (char*)malloc (  ( sizeof( char ) *  ( BUFF_MAX - sizeof( double ) )  )  );
	if( ( op == 't' ) ){

		hcreate ( TEST_MAX ) ;
		e.key = strdup ( testName );
		ep = hsearch ( e , FIND );
		if( ( ep != NULL ) ){

			cnt = (int)( ep->data )++;

		}

		else{

			e.data = (void*)1;
			ep = hsearch ( e , ENTER );

		}

		fseek ( fp , 0L , SEEK_SET ) ;
		while( ( fgets ( buff , sizeof ( buff ) , fp ) != NULL ) ){

			sscanf ( buff , "%lf%s" , &f_cell , tag ) ;
			if( ( strcmp ( tag , testName ) == 0 ) ){

				if( ( cnt != 0 ) ){

					cnt--;
					continue;

				}

				if( ( fabs (  ( cell - f_cell )  ) <= diff ) ){

					printf ( "\x1b[32m %s success\n" , testName ) ;

				}

				else{

					printf ( "\x1b[31m %s fail | input=%lf : file=%lf\n" , testName , cell , f_cell ) ;

				}

				break;

			}


		}


	}

	else if( ( op == 'w' ) ){

		fprintf ( fp , "%lf%s\n" , cell , testName ) ;

	}

	free ( tag ) ;
}


int main ( int argc , char** argv ) {

	int start;
	int end;
	int __i;
	int __j;
	double* xi;
	double* xo;
	double* x1;
	double* x2;
	double* x3;
	double* x4;
	double* x5;
	double* k1;
	double* k2;
	double* k3;
	double* k4;
	double* y1;
	double* y2;
	double* y3;
	double* y4;
	double* z;
	double t;
	double d = 0.010000;
	double* all_xo;
	double* tmp_xo;
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
	testInit ( argv , node ) ;
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
		x4 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
		x5 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
		all_xo = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		tmp_xo = (double*)malloc (  ( sizeof( double ) *  ( 8 * __WORKER_DATA_NUM )  )  ) ;;
		y1 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __MASTER_DATA_NUM )  )  ) ;;
		y2 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __MASTER_DATA_NUM )  )  ) ;;
		y3 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __MASTER_DATA_NUM )  )  ) ;;
		y4 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __MASTER_DATA_NUM )  )  ) ;;
		k1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
		k2 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
		k3 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
		k4 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __MASTER_DATA_NUM )  )  ) ;;
		z = (double*)malloc (  ( sizeof( double ) * 18 )  ) ;;
		for(t = 0.000000; ( t <= 0.100000 ) ;t =  ( t + d ) ){

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
				x2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
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
				x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				y3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
				y3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
				y3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
				y3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
				y3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
				y3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
				y3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
				y3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
				y3[ (  ( 8 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
				y3[ (  ( 9 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
				y3[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
				y3[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y3[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
				y3[ (  ( 12 * __MASTER_DATA_NUM )  + __i ) ] = y3[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ];
				y3[ (  ( 13 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[17] *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - z[16] )  ) ;
				y3[ (  ( 14 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
				y3[ (  ( 15 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
				y3[ (  ( 16 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
				y3[ (  ( 17 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
				y3[ (  ( 18 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
				y3[ (  ( 19 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
				y3[ (  ( 20 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)5.476 )  - y3[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] -  ( y3[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y3[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
				y3[ (  ( 21 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
				y3[ (  ( 22 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[9] * pow( x3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] , (double)3 ) * x3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y3[ (  ( 14 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.09 * x3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 24 * __MASTER_DATA_NUM )  + __i ) ] =  ( y3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] * y3[ (  ( 9 * __MASTER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y3[ (  ( 18 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 25 * __MASTER_DATA_NUM )  + __i ) ] =  ( y3[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] /  ( y3[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] + y3[ (  ( 20 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 26 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
				y3[ (  ( 27 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
				y3[ (  ( 28 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[15] * y3[ (  ( 21 * __MASTER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y3[ (  ( 12 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 29 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
				y3[ (  ( 30 * __MASTER_DATA_NUM )  + __i ) ] =  ( y3[ (  ( 27 * __MASTER_DATA_NUM )  + __i ) ] * y3[ (  ( 25 * __MASTER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y3[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + y3[ (  ( 22 * __MASTER_DATA_NUM )  + __i ) ] + y3[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] + y3[ (  ( 24 * __MASTER_DATA_NUM )  + __i ) ] + y3[ (  ( 30 * __MASTER_DATA_NUM )  + __i ) ] + y3[ (  ( 28 * __MASTER_DATA_NUM )  + __i ) ] + y3[ (  ( 13 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 15 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 26 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 16 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 17 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 29 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 8 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 19 * __MASTER_DATA_NUM )  + __i ) ] * x3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y3[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] * d )  ) ;
				y4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
				y4[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
				y4[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
				y4[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
				y4[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x4[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
				y4[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
				y4[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
				y4[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
				y4[ (  ( 8 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
				y4[ (  ( 9 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
				y4[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
				y4[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y4[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
				y4[ (  ( 12 * __MASTER_DATA_NUM )  + __i ) ] = y4[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ];
				y4[ (  ( 13 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[17] *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - z[16] )  ) ;
				y4[ (  ( 14 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
				y4[ (  ( 15 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
				y4[ (  ( 16 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
				y4[ (  ( 17 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
				y4[ (  ( 18 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
				y4[ (  ( 19 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
				y4[ (  ( 20 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)5.476 )  - y4[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] -  ( y4[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y4[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
				y4[ (  ( 21 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
				y4[ (  ( 22 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[9] * pow( x4[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] , (double)3 ) * x4[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y4[ (  ( 14 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.09 * x4[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y4[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 24 * __MASTER_DATA_NUM )  + __i ) ] =  ( y4[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] * y4[ (  ( 9 * __MASTER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y4[ (  ( 18 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 25 * __MASTER_DATA_NUM )  + __i ) ] =  ( y4[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] /  ( y4[ (  ( 11 * __MASTER_DATA_NUM )  + __i ) ] + y4[ (  ( 20 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 26 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
				y4[ (  ( 27 * __MASTER_DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
				y4[ (  ( 28 * __MASTER_DATA_NUM )  + __i ) ] =  ( z[15] * y4[ (  ( 21 * __MASTER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y4[ (  ( 12 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 29 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
				y4[ (  ( 30 * __MASTER_DATA_NUM )  + __i ) ] =  ( y4[ (  ( 27 * __MASTER_DATA_NUM )  + __i ) ] * y4[ (  ( 25 * __MASTER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] - y4[ (  ( 10 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] + y4[ (  ( 22 * __MASTER_DATA_NUM )  + __i ) ] + y4[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] + y4[ (  ( 24 * __MASTER_DATA_NUM )  + __i ) ] + y4[ (  ( 30 * __MASTER_DATA_NUM )  + __i ) ] + y4[ (  ( 28 * __MASTER_DATA_NUM )  + __i ) ] + y4[ (  ( 13 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 15 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 26 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 16 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 17 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 29 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 8 * __MASTER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 19 * __MASTER_DATA_NUM )  + __i ) ] * x4[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y4[ (  ( 23 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x4[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] )  )  ) ;
				xo[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ] = x5[ (  ( 0 * __MASTER_DATA_NUM )  + __i ) ];
				xo[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ] = x5[ (  ( 1 * __MASTER_DATA_NUM )  + __i ) ];
				xo[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ] = x5[ (  ( 2 * __MASTER_DATA_NUM )  + __i ) ];
				xo[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ] = x5[ (  ( 3 * __MASTER_DATA_NUM )  + __i ) ];
				xo[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ] = x5[ (  ( 4 * __MASTER_DATA_NUM )  + __i ) ];
				xo[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ] = x5[ (  ( 5 * __MASTER_DATA_NUM )  + __i ) ];
				xo[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ] = x5[ (  ( 6 * __MASTER_DATA_NUM )  + __i ) ];
				xo[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ] = x5[ (  ( 7 * __MASTER_DATA_NUM )  + __i ) ];
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

				MPI_Recv ( &tmp_xo[0] ,  ( __WORKER_DATA_NUM * 8 )  , MPI_DOUBLE , worker , tag , MPI_COMM_WORLD , &recv_status ) ;
				__j = 0;
				for(__i = 0; ( __i <  ( __WORKER_DATA_NUM * 8 )  ) ;__i++){

					all_xo[ (  (  (  ( worker - 1 )  * __WORKER_DATA_NUM )  + __MASTER_DATA_NUM )  + __j ) ] = tmp_xo[__i];
					if( (  (  ( __i + 1 )  % __WORKER_DATA_NUM )  == 0 ) ){

						__j =  (  ( __DATA_NUM - __WORKER_DATA_NUM )  + __j ) ;

					}

					__j =  ( __j + 1 ) ;

				}


			}

			testCell ( "test_all_xo0" , all_xo[ ( __DATA_NUM * 0 ) ] , 0.0001 ) ;
			testCell ( "test_all_xo1" , all_xo[ ( __DATA_NUM * 1 ) ] , 0.0001 ) ;
			testCell ( "test_all_xo2" , all_xo[ ( __DATA_NUM * 2 ) ] , 0.0001 ) ;
			testCell ( "test_all_xo3" , all_xo[ ( __DATA_NUM * 3 ) ] , 0.0001 ) ;
			testCell ( "test_all_xo4" , all_xo[ ( __DATA_NUM * 4 ) ] , 0.0001 ) ;
			testCell ( "test_all_xo5" , all_xo[ ( __DATA_NUM * 5 ) ] , 0.0001 ) ;
			testCell ( "test_all_xo6" , all_xo[ ( __DATA_NUM * 6 ) ] , 0.0001 ) ;
			testCell ( "test_all_xo7" , all_xo[ ( __DATA_NUM * 7 ) ] , 0.0001 ) ;

		}

		free ( xi ) ;
		free ( xo ) ;
		free ( x1 ) ;
		free ( x2 ) ;
		free ( x3 ) ;
		free ( x4 ) ;
		free ( x5 ) ;
		free ( y1 ) ;
		free ( y2 ) ;
		free ( y3 ) ;
		free ( y4 ) ;
		free ( k1 ) ;
		free ( k2 ) ;
		free ( k3 ) ;
		free ( k4 ) ;
		free ( z ) ;
		free ( all_xo ) ;
		free ( tmp_xo ) ;

	}

	else{

		xi = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		xo = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		x1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		x2 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		x3 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		x4 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		x5 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		y1 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __DATA_NUM )  )  ) ;;
		y2 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __DATA_NUM )  )  ) ;;
		y3 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __DATA_NUM )  )  ) ;;
		y4 = (double*)malloc (  ( sizeof( double ) *  ( 31 * __DATA_NUM )  )  ) ;;
		k1 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		k2 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		k3 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		k4 = (double*)malloc (  ( sizeof( double ) *  ( 8 * __DATA_NUM )  )  ) ;;
		z = (double*)malloc (  ( sizeof( double ) * 18 )  ) ;;
		for(t = 0.000000; ( t <= 0.100000 ) ;t =  ( t + d ) ){

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
				x2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] +  ( k1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
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
				x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				x3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] +  ( k2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] *  ( d / (double)2 )  )  ) ;
				y3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
				y3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
				y3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
				y3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
				y3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
				y3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
				y3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
				y3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
				y3[ (  ( 8 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
				y3[ (  ( 9 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
				y3[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
				y3[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y3[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
				y3[ (  ( 12 * __WORKER_DATA_NUM )  + __i ) ] = y3[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ];
				y3[ (  ( 13 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[17] *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - z[16] )  ) ;
				y3[ (  ( 14 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
				y3[ (  ( 15 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
				y3[ (  ( 16 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
				y3[ (  ( 17 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
				y3[ (  ( 18 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
				y3[ (  ( 19 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
				y3[ (  ( 20 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)5.476 )  - y3[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] -  ( y3[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y3[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
				y3[ (  ( 21 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
				y3[ (  ( 22 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[9] * pow( x3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] , (double)3 ) * x3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y3[ (  ( 14 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.09 * x3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 24 * __WORKER_DATA_NUM )  + __i ) ] =  ( y3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] * y3[ (  ( 9 * __WORKER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y3[ (  ( 18 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 25 * __WORKER_DATA_NUM )  + __i ) ] =  ( y3[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] /  ( y3[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] + y3[ (  ( 20 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 26 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
				y3[ (  ( 27 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
				y3[ (  ( 28 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[15] * y3[ (  ( 21 * __WORKER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y3[ (  ( 12 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y3[ (  ( 29 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
				y3[ (  ( 30 * __WORKER_DATA_NUM )  + __i ) ] =  ( y3[ (  ( 27 * __WORKER_DATA_NUM )  + __i ) ] * y3[ (  ( 25 * __WORKER_DATA_NUM )  + __i ) ] *  ( x3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y3[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + y3[ (  ( 22 * __WORKER_DATA_NUM )  + __i ) ] + y3[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] + y3[ (  ( 24 * __WORKER_DATA_NUM )  + __i ) ] + y3[ (  ( 30 * __WORKER_DATA_NUM )  + __i ) ] + y3[ (  ( 28 * __WORKER_DATA_NUM )  + __i ) ] + y3[ (  ( 13 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 15 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 26 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 16 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 17 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 29 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y3[ (  ( 8 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y3[ (  ( 19 * __WORKER_DATA_NUM )  + __i ) ] * x3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y3[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
				x4[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] +  ( k3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] * d )  ) ;
				y4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( t >= z[4] )  &&  ( t <= z[5] )  &&  (  (  ( t - z[4] )  -  ( floor(  (  ( t - z[4] )  / z[6] )  ) * z[6] )  )  <= z[7] )  )  ? z[8] : (double)0 ) ;
				y4[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.32 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  /  ( (double)1 - exp(  (  ( - (double)0.1 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)47.13 )  )  ) )  ) ;
				y4[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  ( (double)0.135 * exp(  (  ( (double)80 + x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  /  ( - (double)6.8 )  )  ) )  : (double)0 ) ;
				y4[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  (  (  (  ( - (double)127140 )  * exp(  ( (double)0.2444 * x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  -  ( (double)0.00003474 * exp(  (  ( - (double)0.04391 )  * x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)37.78 )  )  /  ( (double)1 + exp(  ( (double)0.311 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)79.23 )  )  ) )  )  : (double)0 ) ;
				y4[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)7.7 -  ( (double)13.0287 * log(  ( x4[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] / (double)1 )  ) )  ) ;
				y4[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.095 * exp(  (  ( - (double)0.01 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.072 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - (double)5 )  )  ) )  ) ;
				y4[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.012 * exp(  (  ( - (double)0.008 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.15 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)28 )  )  ) )  ) ;
				y4[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.282 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
				y4[ (  ( 8 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0005 * exp(  ( (double)0.083 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.057 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)50 )  )  ) )  ) ;
				y4[ (  ( 9 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] >  ( - (double)100 )  )  ?  (  ( (double)2.837 *  ( exp(  ( (double)0.04 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)77 )  )  ) - (double)1 )  )  /  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)77 )  * exp(  ( (double)0.04 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)35 )  )  ) )  )  : (double)1 ) ;
				y4[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[13] / z[14] )  ) ) ;
				y4[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)1.02 /  ( (double)1 + exp(  ( (double)0.2385 *  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y4[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  - (double)59.215 )  )  ) )  ) ;
				y4[ (  ( 12 * __WORKER_DATA_NUM )  + __i ) ] = y4[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ];
				y4[ (  ( 13 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[17] *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - z[16] )  ) ;
				y4[ (  ( 14 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  ( z[10] / z[11] )  ) ) ;
				y4[ (  ( 15 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.08 * exp(  (  ( - x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  / (double)11 )  ) ) ;
				y4[ (  ( 16 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)0.1212 * exp(  (  ( - (double)0.01052 )  * x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1378 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)40.14 )  )  ) )  )  :  (  ( (double)0.3 * exp(  (  ( - (double)0.0000002535 )  * x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.1 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)32 )  )  ) )  )  ) ;
				y4[ (  ( 17 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.07 * exp(  (  ( - (double)0.017 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  /  ( (double)1 + exp(  ( (double)0.05 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)44 )  )  ) )  ) ;
				y4[ (  ( 18 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( z[0] * z[1] )  / z[2] )  * log(  (  ( z[13] +  ( z[12] * z[10] )  )  /  ( z[14] +  ( z[12] * z[11] )  )  )  ) ) ;
				y4[ (  ( 19 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0013 * exp(  (  ( - (double)0.06 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.04 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)20 )  )  ) )  ) ;
				y4[ (  ( 20 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( (double)0.49124 * exp(  ( (double)0.08032 *  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)5.476 )  - y4[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) )  +  ( (double)1 * exp(  ( (double)0.06175 *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] -  ( y4[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] + (double)594.31 )  )  )  ) )  )  /  ( (double)1 + exp(  (  ( - (double)0.5143 )  *  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y4[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  + (double)4.753 )  )  ) )  ) ;
				y4[ (  ( 21 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)1 /  ( (double)1 + exp(  (  ( (double)7.488 - x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  / (double)5.98 )  ) )  ) ;
				y4[ (  ( 22 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[9] * pow( x4[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] , (double)3 ) * x4[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y4[ (  ( 14 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.09 * x4[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y4[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 24 * __WORKER_DATA_NUM )  + __i ) ] =  ( y4[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] * y4[ (  ( 9 * __WORKER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y4[ (  ( 18 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 25 * __WORKER_DATA_NUM )  + __i ) ] =  ( y4[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] /  ( y4[ (  ( 11 * __WORKER_DATA_NUM )  + __i ) ] + y4[ (  ( 20 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 26 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] <  ( - (double)40 )  )  ?  (  ( (double)3.56 * exp(  ( (double)0.079 * x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  +  ( (double)310000 * exp(  ( (double)0.35 * x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  ) )  )  :  ( (double)1 /  ( (double)0.13 *  ( (double)1 + exp(  (  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)10.66 )  /  ( - (double)11.1 )  )  ) )  )  )  ) ;
				y4[ (  ( 27 * __WORKER_DATA_NUM )  + __i ) ] =  ( (double)0.6047 * sqrt(  ( z[13] / (double)5.4 )  ) ) ;
				y4[ (  ( 28 * __WORKER_DATA_NUM )  + __i ) ] =  ( z[15] * y4[ (  ( 21 * __WORKER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y4[ (  ( 12 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				y4[ (  ( 29 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( (double)0.0065 * exp(  (  ( - (double)0.02 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  /  ( (double)1 + exp(  (  ( - (double)0.2 )  *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + (double)30 )  )  ) )  ) ;
				y4[ (  ( 30 * __WORKER_DATA_NUM )  + __i ) ] =  ( y4[ (  ( 27 * __WORKER_DATA_NUM )  + __i ) ] * y4[ (  ( 25 * __WORKER_DATA_NUM )  + __i ) ] *  ( x4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] - y4[ (  ( 10 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  ( - (double)1 )  / z[3] )  *  ( y4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] + y4[ (  ( 22 * __WORKER_DATA_NUM )  + __i ) ] + y4[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] + y4[ (  ( 24 * __WORKER_DATA_NUM )  + __i ) ] + y4[ (  ( 30 * __WORKER_DATA_NUM )  + __i ) ] + y4[ (  ( 28 * __WORKER_DATA_NUM )  + __i ) ] + y4[ (  ( 13 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 15 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 26 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 16 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 17 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 29 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  (  ( y4[ (  ( 8 * __WORKER_DATA_NUM )  + __i ) ] *  ( (double)1 - x4[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  )  -  ( y4[ (  ( 19 * __WORKER_DATA_NUM )  + __i ) ] * x4[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  ) ;
				k4[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  (  (  (  ( - (double)0.0001 )  / (double)1 )  * y4[ (  ( 23 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)0.07 *  ( (double)0.0001 - x4[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				x5[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] =  ( x1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] +  (  ( d / (double)6 )  *  ( k1[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] +  ( (double)2 * k2[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] )  +  ( (double)2 * k3[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] )  + k4[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] )  )  ) ;
				xo[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] = x5[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ];
				xo[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] = x5[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ];
				xo[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] = x5[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ];
				xo[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] = x5[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ];
				xo[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] = x5[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ];
				xo[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] = x5[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ];
				xo[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] = x5[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ];
				xo[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] = x5[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ];
				xi[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 0 * __WORKER_DATA_NUM )  + __i ) ];
				xi[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 1 * __WORKER_DATA_NUM )  + __i ) ];
				xi[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 2 * __WORKER_DATA_NUM )  + __i ) ];
				xi[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 3 * __WORKER_DATA_NUM )  + __i ) ];
				xi[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 4 * __WORKER_DATA_NUM )  + __i ) ];
				xi[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 5 * __WORKER_DATA_NUM )  + __i ) ];
				xi[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 6 * __WORKER_DATA_NUM )  + __i ) ];
				xi[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ] = xo[ (  ( 7 * __WORKER_DATA_NUM )  + __i ) ];

			}

			MPI_Send ( &xo[start] ,  ( __WORKER_DATA_NUM * 8 )  , MPI_DOUBLE , MASTER , tag , MPI_COMM_WORLD ) ;

		}

		free ( xi ) ;
		free ( xo ) ;
		free ( x1 ) ;
		free ( x2 ) ;
		free ( x3 ) ;
		free ( x4 ) ;
		free ( x5 ) ;
		free ( y1 ) ;
		free ( y2 ) ;
		free ( y3 ) ;
		free ( y4 ) ;
		free ( k1 ) ;
		free ( k2 ) ;
		free ( k3 ) ;
		free ( k4 ) ;
		free ( z ) ;

	}

	MPI_Finalize (  ) ;
}



