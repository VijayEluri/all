#!/bin/sh
mdls $1 | grep kMDItemNumberOfPages | awk '{print $3}'
