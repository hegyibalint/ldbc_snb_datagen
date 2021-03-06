![LDBC_LOGO](https://raw.githubusercontent.com/wiki/ldbc/ldbc_snb_datagen/images/ldbc-logo.png)

LDBC-SNB Data Generator
----------------------

[![Build Status](https://travis-ci.org/ldbc/ldbc_snb_datagen.svg?branch=master)](https://travis-ci.org/ldbc/ldbc_snb_datagen)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5b0c677c9c4c4de3b6af15f118c9212c)](https://www.codacy.com/app/ArnauPrat/ldbc_snb_datagen?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ldbc/ldbc_snb_datagen&amp;utm_campaign=Badge_Grade)

The LDBC-SNB Data Generator (DATAGEN) is the responsible of providing the data sets used by all the LDBC benchmarks. This data generator is designed to produce directed labeled graphs that mimic the characteristics of those graphs of real data. A detailed description of the schema produced by datagen, as well as the format of the output files, can be found in the latest version of official [LDBC SNB specification document](https://github.com/ldbc/ldbc_snb_docs).


ldbc_snb_datagen is part of the [LDBC project](http://www.ldbc.eu/).
ldbc_snb_datagen is GPLv3 licensed, to see detailed information about this license read the `LICENSE.txt` file.

* **[Releases](https://github.com/ldbc/ldbc_snb_datagen/releases)**
* **[Configuration](https://github.com/ldbc/ldbc_snb_datagen/wiki/Configuration)**
* **[Compilation and Execution](https://github.com/ldbc/ldbc_snb_datagen/wiki/Compilation_Execution)**
* **[Advanced Configuration](https://github.com/ldbc/ldbc_snb_datagen/wiki/Advanced_Configuration)**
* **[Output](https://github.com/ldbc/ldbc_snb_datagen/wiki/Data-Output)**
* **[Troubleshooting](https://github.com/ldbc/ldbc_snb_datagen/wiki/Troubleshooting)**

## Quick start

```bash
wget http://archive.apache.org/dist/hadoop/core/hadoop-2.6.0/hadoop-2.6.0.tar.gz
tar xf hadoop-2.6.0.tar.gz
export HADOOP_CLIENT_OPTS="-Xmx2G"
# set this to the Hadoop 2.6.0 directory
export HADOOP_HOME=
# set this to the repository's directory
export LDBC_SNB_DATAGEN_HOME=
cd $LDBC_SNB_DATAGEN_HOME
./run.sh
```

## Docker image

The image can be simply built with the provided Dockerfile.
To build, execute the following command from the repository directory:
```
docker build . --tag ldbc/datagen
```

### Options

To configure the amount of memory available, set the `HADOOP_CLIENT_OPTS` variable in the Dockerfile. The default value is `-Xmx8G`.

### Running

In order to run the container, a `params.ini` file is required. For reference, please see the `params*.ini` files in the repository. The file will be mounted in the container by the `--mount type=bind,source="$(pwd)/params.ini,target="/opt/ldbc_snb_datagen/params.ini"` option. If required, the source path can be set to a different path.

The container will output it's results in the `/opt/ldbc_snb_datagen/social_network/` directory. In order to save the results of the generation, a directory must be mounted in the container from the host:

```
mkdir datagen_output

docker run --rm --mount type=bind,source="$(pwd)/datagen_output/",target="/opt/ldbc_snb_datagen/social_network/" --mount type=bind,source="$(pwd)/params.ini",target="/opt/ldbc_snb_datagen/params.ini" ldbc/datagen
```

If the memory limit has to be raised, the `-e HADOOP_CLIENT_OPTS="-Xmx..."` parameter can override the default `-Xmx8G` value.

<!-- Publicly available datasets can be found at the LDBC-SNB Amazon Bucket. These datasets are the official SNB datasets and were  generated using version 0.2.6. They are available in the three official supported serializers: CSV, CSVMergeForeign and TTL. The bucket is configured in "Requester Pays" mode, thus in order to access them you need a properly set up AWS client.
* http://ldbc-snb.s3.amazonaws.com/ -->

**Community provided tools**


* **[Apache Flink Loader:](https://github.com/s1ck/ldbc-flink-import)** A loader of LDBC datasets for Apache Flink.
