#initialise i for iterations
BEGIN{
i=0;
}

{
#calculation for obtaining the run time goes here.
t=$3;
starttime[i]=t;
if(t==30)
{
spl=starttime[i-1];
}
i++;
}

END{
#saving the value to the file, in calculate_est >> thr. 
l=starttime[length(starttime)-1];
print("%f",l);
printf(" %f \n",spl);
printf(" %f \n",(spl+l)-30.0);
}
