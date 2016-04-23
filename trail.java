import java.io.*;
import java.util.Scanner;
public class trail
{
  public static void main(String []args){
          Scanner in = new Scanner(System.in);
          int n=0,k=0,r=0;

  try
  {
  System.out.println("enter n");
   n = in.nextInt();
   int arr[]=new int[n];
  System.out.println("enter diffrence");
   k = in.nextInt();
   System.out.println("Enter elements");
    for (int c=0;c<n;c++)
          arr[c]=in.nextInt();


  for (int i =0;i<n;i++ ){
    for (int j=i+1;j<n;j++)
    {
      if((arr[i]-arr[j])==-k)
      {
      r+=1;
      System.out.println("arr[i]");
      }
    }}
     System.out.println("The count is"+r);
    }catch (Exception e)
  {
  System.out.println("Input integer"+ e);

  }

  }

}
