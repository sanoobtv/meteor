BEGIN{
i=0;
}

{
t=$3;
starttime[i]=t;

if(t==30)
{
spl=starttime[i-1];
}
i++;
}

END{
l=starttime[length(starttime)-1];
print("%f",l);
printf(" %f \n",spl);
printf(" %f \n",(spl+l)-30.0);
}
