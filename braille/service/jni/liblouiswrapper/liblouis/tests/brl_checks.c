#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include "liblouis.h"
#include "louis.h"
#include "brl_checks.h"

void
print_int_array(const char *prefix, int *pos_list, int len)
{
  int i;
  printf("%s ", prefix);
  for (i = 0; i < len; i++)
    printf("%d ", pos_list[i]);
  printf("\n");
}

void
print_widechars(widechar * buf, int len)
{
  int i;
  for (i = 0; i < len; i++)
    printf("%c", buf[i]);
}

/* Check if a string is translated as expected. Return 0 if the
   translation is as expected and 1 otherwise. */
int
check_translation(const char *tableList, const char *str,
		  const char *typeform, const char *expected)
{
  return check_translation_with_mode(tableList, str, typeform, expected, 0);
}

/* Check if a string is translated as expected. Return 0 if the
   translation is as expected and 1 otherwise. */
int
check_translation_with_mode(const char *tableList, const char *str,
			    const char *typeform, const char *expected, 
			    int mode)
{
  widechar *inbuf;
  widechar *outbuf;
  int inlen;
  int outlen;
  int i, rv = 0;

  char *typeformbuf = NULL;

  int expectedlen = strlen(expected);

  inlen = strlen(str) * 2;
  outlen = inlen;
  inbuf = malloc(sizeof(widechar) * inlen);
  outbuf = malloc(sizeof(widechar) * outlen);
  if (typeform != NULL)
    {
      typeformbuf = malloc(outlen);
      strcpy(typeformbuf, typeform);
    }
  if (!extParseChars(str, inbuf))
    {
      printf("Cannot parse input string.\n");
      return 1;
    }
  if (!lou_translate(tableList, inbuf, &inlen, outbuf, &outlen,
		     typeformbuf, NULL, NULL, NULL, NULL, mode))
    {
      printf("Translation failed.\n");
      return 1;
    }

  for (i = 0; i < outlen && i < expectedlen && expected[i] == outbuf[i]; i++);
  if (i < outlen || i < expectedlen)
    {
      rv = 1;
      outbuf[outlen] = 0;
      printf("Input: '%s'\n", str);
      printf("Expected: '%s'\n", expected);
      printf("Received: '");
      print_widechars(outbuf, outlen);
      printf("'\n");
      if (i < outlen && i < expectedlen) 
	{
	  printf("Diff: Expected '%c' but recieved '%c' in index %d\n",
		 expected[i], outbuf[i], i);
	}
      else if (i < expectedlen)
	{
	  printf("Diff: Expected '%c' but recieved nothing in index %d\n",
		 expected[i], i);
	}
      else 
	{
	  printf("Diff: Expected nothing but recieved '%c' in index %d\n",
		  outbuf[i], i);
	}
    }

  free(inbuf);
  free(outbuf);
  free(typeformbuf);
  lou_free();
  return rv;
}

int
check_inpos(const char *tableList, const char *str, const int *expected_poslist)
{
  widechar *inbuf;
  widechar *outbuf;
  int *inpos, *outpos;
  int inlen;
  int outlen;
  int i, rv = 0;

  inlen = strlen(str) * 2;
  outlen = inlen;
  inbuf = malloc(sizeof(widechar) * inlen);
  outbuf = malloc(sizeof(widechar) * outlen);
  inpos = malloc(sizeof(int) * inlen);
  for (i = 0; i < inlen; i++)
    {
      inbuf[i] = str[i];
    }
  lou_translate(tableList, inbuf, &inlen, outbuf, &outlen,
		NULL, NULL, NULL, inpos, NULL, 0);
  for (i = 0; i < outlen; i++)
    {
      if (expected_poslist[i] != inpos[i])
	{
	  rv = 1;
	  printf("Expected %d, recieved %d in index %d\n",
		 expected_poslist[i], inpos[i], i);
	}
    }

  free(inbuf);
  free(outbuf);
  free(inpos);
  lou_free();
  return rv;

}

int
check_outpos(const char *tableList, const char *str, const int *expected_poslist)
{
  widechar *inbuf;
  widechar *outbuf;
  int *inpos, *outpos;
  int origInlen, inlen;
  int outlen;
  int i, rv = 0;

  origInlen = inlen = strlen(str);
  outlen = inlen * 2;
  inbuf = malloc(sizeof(widechar) * inlen);
  outbuf = malloc(sizeof(widechar) * outlen);
  /* outputPos can be affected by inputPos, so pass inputPos as well. */
  inpos = malloc(sizeof(int) * outlen);
  outpos = malloc(sizeof(int) * inlen);
  for (i = 0; i < inlen; i++)
    {
      inbuf[i] = str[i];
    }
  lou_translate(tableList, inbuf, &inlen, outbuf, &outlen,
		NULL, NULL, outpos, inpos, NULL, 0);
  if (inlen != origInlen)
    {
      printf("original inlen %d and returned inlen %d differ\n",
        origInlen, inlen);
    }

  for (i = 0; i < inlen; i++)
    {
      if (expected_poslist[i] != outpos[i])
	{
	  rv = 1;
	  printf("Expected %d, recieved %d in index %d\n",
		 expected_poslist[i], outpos[i], i);
	}
    }

  free(inbuf);
  free(outbuf);
  free(inpos);
  free(outpos);
  lou_free();
  return rv;

}

int
check_cursor_pos(const char *str, const int *expected_pos)
{
  widechar *inbuf;
  widechar *outbuf;
  int *inpos, *outpos;
  int orig_inlen;
  int outlen;
  int cursor_pos;
  int i, rv = 0;

  orig_inlen = strlen(str);
  outlen = orig_inlen;
  inbuf = malloc(sizeof(widechar) * orig_inlen);
  outbuf = malloc(sizeof(widechar) * orig_inlen);
  inpos = malloc(sizeof(int) * orig_inlen);
  outpos = malloc(sizeof(int) * orig_inlen);
  for (i = 0; i < orig_inlen; i++)
    {
      inbuf[i] = str[i];
    }

  for (i = 0; i < orig_inlen; i++)
    {
      int inlen = orig_inlen;
      cursor_pos = i;
      lou_translate(TRANSLATION_TABLE, inbuf, &inlen, outbuf, &outlen,
		    NULL, NULL, NULL, NULL, &cursor_pos, compbrlAtCursor);
      if (expected_pos[i] != cursor_pos)
	{
	  rv = 1;
	  printf("string='%s' cursor=%d ('%c') expected=%d \
recieved=%d ('%c')\n", str, i, str[i], expected_pos[i], cursor_pos, (char) outbuf[cursor_pos]);
	}
    }

  free(inbuf);
  free(outbuf);
  free(inpos);
  free(outpos);
  lou_free();

  return rv;
}

/* Check if a string is hyphenated as expected. Return 0 if the
   hyphenation is as expected and 1 otherwise. */
int
check_hyphenation(const char *tableList, const char *str, const char *expected)
{
  widechar *inbuf;
  char *hyphens;
  int inlen;
  int rv = 0;

  inlen = strlen(str);
  inbuf = malloc(sizeof(widechar) * (inlen + 1));
  if (!extParseChars(str, inbuf))
    {
      printf("Cannot parse input string.\n");
      return 1;
    }
  hyphens = malloc(sizeof(char) * (inlen + 1));

  if (!lou_hyphenate(tableList, inbuf, inlen, hyphens, 0))
    {
      printf("Hyphenation failed.\n");
      return 1;
    }

  if (strcmp(expected, hyphens))
    {
      printf("Input:    '%s'\n", str);
      printf("Expected: '%s'\n", expected);
      printf("Received: '%s'\n", hyphens);
      rv = 1;
    }

  free(inbuf);
  free(hyphens);
  lou_free();
  return rv;

}
