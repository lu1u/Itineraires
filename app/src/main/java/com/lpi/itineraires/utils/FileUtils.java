package com.lpi.itineraires.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


/**
 * Created by lucien on 15/01/2018.
 */

public class FileUtils
{
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private final static int[] illegalChars = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};

	static
	{
		Arrays.sort(illegalChars);
	}

	/***
	 * Lire le contenu d'un InputStream et le copier dans un tableau d'octets
	 * @param is
	 * @return
	 */
	public static byte[] readArray(@NonNull InputStream is)
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try
		{
			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1)
			{
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return buffer.toByteArray();
	}


	/***
	 * Change l'extension d'un nom de fichier
	 * @param name
	 * @param extension
	 * @return
	 */
	@NonNull
	public static String changeExtension(@NonNull String name, @NonNull String extension)
	{
		int indicePoint = name.lastIndexOf('.');
		if (indicePoint != -1)
		{
			int indiceSlash = name.lastIndexOf('/');
			if (indiceSlash < indicePoint)
				name = name.substring(0, indicePoint + 1) + extension;
		}

		return name;
	}


	/***
	 * Lit une image dans un fichier
	 * @param is InputStream
	 * @return Bitmap lue
	 */
	public static Bitmap readImage(@NonNull InputStream is)
	{
		byte byteArray[] = FileUtils.readArray(is);
		return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
	}

	/***
	 * Supprime les caracteres illegaux d'un nom de fichier
	 * @param badFileName
	 * @return
	 */
	public static String cleanFileName(String badFileName)
	{
		StringBuilder cleanName = new StringBuilder();
		for (int i = 0; i < badFileName.length(); i++)
		{
			int c = (int) badFileName.charAt(i);
			if (Arrays.binarySearch(illegalChars, c) < 0)
			{
				cleanName.append((char) c);
			}
		}
		return cleanName.toString();
	}

	/***
	 * Verifie q'un nom de fichier est legals
	 * @param fileName
	 * @return
	 */
	public static boolean fileNameOk(String fileName)
	{
		for (int i = 0; i < fileName.length(); i++)
		{
			int c = (int) fileName.charAt(i);
			if (Arrays.binarySearch(illegalChars, c) >= 0)
				return false;
		}

		return true;
	}

	/***
	 * Copie d'un fichier
	 * @param i : fichier d'entree
	 * @param o : fichier de sortie
	 * @throws IOException
	 */
	public static void copyLarge(InputStream i, OutputStream o) throws IOException
	{
		BufferedOutputStream output = new BufferedOutputStream(o);
		BufferedInputStream input = new BufferedInputStream(i);

		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int n;
		while (-1 != (n = input.read(buffer)))
		{
			output.write(buffer, 0, n);
		}
	}

	/***
	 * Retourne vrai si le nom de fichier se termine par l'extension donnee
	 * @param name
	 * @param extension
	 * @return
	 */
	public static boolean extensionOK(@Nullable String name, @Nullable String extension)
	{
		if (extension == null)
			return true;

		if (name == null)
			return false;

		if (name.length() < extension.length())
			return false;

		if (!extension.startsWith("."))
			extension = '.' + extension;

		int i = name.lastIndexOf(extension);
		if (i == -1)
			return false;

		return i == (name.length() - extension.length());
	}


}
