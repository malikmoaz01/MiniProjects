//BLOOD DONOR SYSTEm
#include<iostream>
#include<fstream>
#include<cmath>
#include<iomanip>
using namespace std;
struct donor
{
	string name;
	string blood;
	string city;
};
void input(donor arr[100])
{
	int members;
	ofstream file;
	file.open("bds_donor_data.txt" , ios::app);
	cout<<"Enter no of members u wish u want to add in this : ";
	cin>>members;
	cout<<endl;
	for(int h=0; h<members; h++)
	{
		cout<<"Member <<"<<h+1<<">>"<<endl;
		cin.ignore();
		cout<<"Name : ";
		getline(cin,arr[h].name);
		cout<<endl;
		cout<<"Blood group : ";
		cin>>arr[h].blood;
		cout<<endl;
		cout<<"city : ";
		cin>>arr[h].city;
		file<<arr[h].name << "\n";
		file<<arr[h].blood << "\n";
		file<<arr[h].city<<endl;
        ((h+1 == members)?"":"\n");
	}
	file.close();
}

void searchName(string a , donor arr[100] , int n, int m)
{
	cout<<"Enter Name u want to search data: ";
	ifstream file;
	cin>>a;
	file.open("bds_donor_data.txt");
	while(!file.eof())
	{
		getline(file,arr[n].name);
		getline(file,arr[n].blood);
		getline(file,arr[n].city);
		n++;
	}
	int f;
	for(f = 0; f<n; f++)
	{
		if(a == arr[f].name){
			cout<<"Name : "<<arr[f].name<<endl;
			cout<<"BLOOD group : "<<arr[f].blood<<endl;
			cout<<"city : "<<arr[f].city;
			f = 0;
			break;
		}
	}
	if (n == f){
		cout<<"\n";
		cout<<"***No name searched***\n";
	}
}
void searchblood(string a, donor arr[100], int n , int m)
{
	cout<<"Enter Blood group u want to search: ";
	ifstream file;
    cin>>a;
	file.open("bds_donor_data.txt");
	while(!file.eof()){
		getline(file,arr[n].name);
		getline(file,arr[n].blood);
		getline(file,arr[n].city);
		n++;
	}
	int f;
	for(f=0; f<n; f++)
	{
		if(a == arr[f].blood){
		cout<<"Name : "<<arr[f].name<<endl;
		cout<<"BLOOD group : "<<arr[f].blood<<endl;
		cout<<"city : "<<arr[f].city;
		f = 0;
		break;
			}
	}
	if (n==f)
	{
		cout<<"***No name searched ***";
	}
}
void update(string a,donor arr[100] , int n , int m)
{
	cin.ignore();
	cout << "Enter Name u want to update: ";
	getline(cin,a);
	ifstream file;
	file.open("bds_donor_data.txt");
	while(!file.eof())
	{
		getline(file,arr[n].name);
		getline(file,arr[n].blood);
		getline(file,arr[n].city);
		n++;
	}
	
	for(int f=0; f<n; f++)
	if(a == arr[f].name)
	{
		cout<<"Name : \t \n";
		getline(cin,arr[f].name);
	    cout<<"Blood group : \n";
		getline(cin,arr[f].blood);
		cout<<"City : \n";
		getline(cin,arr[f].city);
		ofstream temp;
		temp.open("bds_donor_data.txt");	
			for(int h=0; h<n; h++)
	{
		temp<<arr[h].name << "\n";
		temp<<arr[h].blood << "\n";
		temp<<arr[h].city << ((h+1 == n)?"":"\n");
	}
	temp.close();
		return;
	}
	cout << "No record found!!!!\n";
}
void remove(string a,donor arr[100] , int n)
{
	string name;
	cout<<"Enter name : ";
	cin >> name;
	ifstream file;
	file.open("bds_donor_data.txt");
	ofstream tip("temp.txt");
	donor d2[100];
	while(!file.eof())
	{
		getline(file,d2[n].name);
		getline(file,d2[n].blood);
		getline(file,d2[n].city);
		if(name != d2[n].name)
	{
		tip<<d2[n].name<<endl;
		tip<<d2[n].blood<<endl;
		tip<<d2[n].city<<endl;
	}               
    n++;
}
	file.close();
    tip.close();
    remove("bds_donor_data.txt");
    rename("temp.txt","bds_donor_data.txt");
}
void read(donor arr[100],int n)
{
	ifstream file;
	file.open("bds_donor_data.txt");
	int i = 0;
	while(file){
	getline(file,arr[i].name);
	getline(file,arr[i].blood);
	getline(file,arr[i].city);
	i++;
}
    i--;
	file.close();
    cout<<endl;
	string equal;
	equal.assign(20,'=');
	for(int h=0; h<i; h++)
	{	
		if (arr[h].name == "") 
		  break; 
		cout<<"Name : "<<arr[h].name<<endl;
		cout<<"blood group : "<<arr[h].blood<<endl;
		cout<<"city : "<<arr[h].city<<endl;
		cout<<equal<<endl;
	}
}

int main()
{	
	
	donor d[100];
	donor d1[100];
	string name , blood , name1;
	int i=0 , j=0 , k=0 , l=0 , m=0 , n=0 , o=0;
	cout<<endl<<endl;
	cout<<"      *****PUCIT BLOOD DONOR SOCIETY**** ";
	cout<<endl;
	while(true)
	{
		cout<<endl;
		cout<<"\t\t1.ADD A MEMBER \n\n";
		cout<<"\t\t2.Search a list of donors by name \n\n";
		cout<<"\t\t3.Search a list of donors by blood group \n\n";
		cout<<"\t\t4.Update the donor record\n\n";
		cout<<"\t\t5.Remove donor record\n\n";
		cout<<"\t\t6.View list of donors\n\n";
		cout<<"\t\t7.EXIT THE PROGRAM\n\n";
		int choice; 
		cout<<"Ur choice : ";
		cin>>choice;
		while(choice <= 0 || choice >7)
		{
			cout<<"***Enter option btw 1 & 6***";
			cin>>choice;
		}
		if(choice==1)
		{
			input(d);
			i++;
			continue;
		}
		else if(choice==2)
		{
			searchName(name,d,i,k);
			continue;
		}
		else if(choice==3)
		{
			searchblood(blood,d1,l,m);
			continue;
		}
		else if(choice==4)
		{
			update(name1,d,n,o);
			continue;
		}
		else if(choice == 5)
		{
			remove(name,d,i);
			continue;
		}
		else if(choice==6)
		{
			read(d,j);
			continue;
		}
		else if(choice==7)
		{
		cout<<"Have a good Day ";
			break;
		}		
	}	
}
