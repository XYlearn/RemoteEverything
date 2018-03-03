#!/bin/sh
#socat tcp-l:15536,fork,reuseaddr exec:bin/everything 2>/dev/null 
socat tcp-l:15536,fork,reuseaddr exec:bin/everything
