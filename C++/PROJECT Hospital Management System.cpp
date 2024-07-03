#include<stdio.h>    
#include<iostream> 
#include<fstream> 
#include<string> 
using namespace std; 
	

void doctordetails(){
	char name[50],field[50];
	int num,age;
	fstream doctor;
	int doctordetail;
		cout<<"\t***************  Doctor Details  *****************\n\n\n";
		cout<<"1. Add New Doctor\n\n2. View All Doctors.\n\n";
		cin>>doctordetail;
		system ("cls");
		switch(doctordetail){
		case 1:
		ofstream doctor;
		doctor.open("doctordetails.txt");
		cin.sync();
		cout<<"\nEnter The Doctor Name = ";
		cin.getline(name,50);
		cout<<"\nEnter the Doctors Field = ";
		cin.getline(field,50);
		cout<<"\nEnter Age = ";
		cin>>age;
		cout<<"\nEnter Experience(in years) = ";
		cin>>num;
		system("cls");
		cout<<"\n\nYour Entry Has been saved ";

		break;

				
		}
}
void patientdetails(){
		cout<<"\t***************  Patient Details  *****************\n\n\n";
		cout<<"1. Add New Patient\n\n2. View Patient Details.";
}
void hospitalservices(){
		cout<<"\t***************  Hospital Services  *****************\n\n\n";
		cout<<"1. Add New Hospital Services\n\n2. View Hospital Services.";
}
int main(){
	int choice1, choice2;
	cout<<"\t***************  Hospital Managment System  *****************\n\n\n";
	cout<<"\n\nPlease Choose from the Menu\n\n1. Admin\n\n2. Doctor\n\n3. Patient\n\n";
	cin>>choice1;
	system("cls");
	if(choice1==1){	
		cout<<"\t***************  Admin  *****************\n\n\n";
		cout<<"1. Doctor Details\n\n2. Patient Details\n\n3. Hospital Services\n\n";
		cin>>choice2;
		system("cls");
		switch(choice2)
		{
		case 1: 
		doctordetails();
		break;
		case 2: 
		patientdetails();
		break;
		case 3: 
		hospitalservices();
		break;
		default:
			cout<<"Error!! Please Choose from the menu";
		break;
		}

		}		
 return 0;
}