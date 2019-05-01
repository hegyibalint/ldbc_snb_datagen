#!/bin/bash

# Running the generator
/opt/hadoop-2.6.0/bin/hadoop jar /opt/ldbc_snb_datagen/target/ldbc_snb_datagen-0.2.7-jar-with-dependencies.jar /opt/ldbc_snb_datagen/params.ini

# Cleanup
rm -f m*personFactors*
rm -f .m*personFactors*
rm -f m*activityFactors*
rm -f .m*activityFactors*
rm -f m0friendList*
rm -f .m0friendList*
