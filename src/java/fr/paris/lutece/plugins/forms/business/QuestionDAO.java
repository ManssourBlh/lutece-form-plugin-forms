/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.forms.business;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Question objects
 */
public final class QuestionDAO implements IQuestionDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_question, title, code, description, id_entry, id_step, is_visible_multiview_global, is_visible_multiview_form_selected, column_title FROM forms_question";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_question = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_question ( title, code, description, id_entry, id_step, is_visible_multiview_global, is_visible_multiview_form_selected, column_title ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_question WHERE id_question = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_question SET id_question = ?, title = ?, code = ?, description = ?, id_entry = ?, id_step = ?, is_visible_multiview_global = ?, is_visible_multiview_form_selected = ?, column_title = ? WHERE id_question = ?";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_question FROM forms_question";
    private static final String SQL_QUERY_SELECT_BY_STEP = SQL_QUERY_SELECTALL + " WHERE id_step = ?";
    private static final String SQL_QUERY_SELECTALL_BY_FORM = "SELECT fq.id_question, fq.title, fq.code, fq.description, fq.id_entry, fq.id_step, fq.is_visible_multiview_global, fq.is_visible_multiview_form_selected , fq.column_title FROM forms_question fq INNER JOIN forms_step fs ON fq.id_step = fs.id_step WHERE fs.id_form = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Question question, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin );
        try
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, question.getTitle( ) );
            daoUtil.setString( nIndex++, question.getCode( ) );
            daoUtil.setString( nIndex++, question.getDescription( ) );
            daoUtil.setInt( nIndex++, question.getIdEntry( ) );
            daoUtil.setInt( nIndex++, question.getIdStep( ) );
            daoUtil.setBoolean( nIndex++, question.isVisibleMultiviewGlobal( ) );
            daoUtil.setBoolean( nIndex++, question.isVisibleMultiviewFormSelected( ) );
            daoUtil.setString( nIndex++, question.getColumnTitle( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                question.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        finally
        {
            daoUtil.close( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Question load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );
        Question question = null;

        if ( daoUtil.next( ) )
        {
            question = dataToObject( daoUtil );
        }

        daoUtil.close( );
        return question;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate( );
        daoUtil.close( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Question question, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, question.getId( ) );
        daoUtil.setString( nIndex++, question.getTitle( ) );
        daoUtil.setString( nIndex++, question.getCode( ) );
        daoUtil.setString( nIndex++, question.getDescription( ) );
        daoUtil.setInt( nIndex++, question.getIdEntry( ) );
        daoUtil.setInt( nIndex++, question.getIdStep( ) );
        daoUtil.setBoolean( nIndex++, question.isVisibleMultiviewGlobal( ) );
        daoUtil.setBoolean( nIndex++, question.isVisibleMultiviewFormSelected( ) );
        daoUtil.setString( nIndex++, question.getColumnTitle( ) );
        daoUtil.setInt( nIndex, question.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.close( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Question> selectQuestionsList( Plugin plugin )
    {
        List<Question> questionList = new ArrayList<Question>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            questionList.add( dataToObject( daoUtil ) );
        }

        daoUtil.close( );
        return questionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Question> selectQuestionsListByStep( int nIdStep, Plugin plugin )
    {
        List<Question> questionList = new ArrayList<Question>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STEP, plugin );
        daoUtil.setInt( 1, nIdStep );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            questionList.add( dataToObject( daoUtil ) );
        }

        daoUtil.close( );
        return questionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdQuestionsList( Plugin plugin )
    {
        List<Integer> questionList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            questionList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.close( );
        return questionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectQuestionsReferenceList( Plugin plugin )
    {
        ReferenceList questionList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            questionList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.close( );
        return questionList;
    }

    @Override
    public List<Question> selectQuestionsListByFormId( int nIdForm, Plugin plugin )
    {
        List<Question> questionList = new ArrayList<Question>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            questionList.add( dataToObject( daoUtil ) );
        }

        daoUtil.close( );
        return questionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectQuestionsReferenceListByForm( int nIdForm, Plugin plugin )
    {
        ReferenceList questionList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            questionList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.close( );
        return questionList;
    }

    /**
     * @param nIdEntry
     *            the entry primary key
     * @return the Entry
     */
    private Entry getQuestionEntry( int nIdEntry )
    {
        return EntryHome.findByPrimaryKey( nIdEntry );
    }

    /**
     * @param nIdStep
     *            the step primary key
     * @return the Step
     */
    private Step getQuestionStep( int nIdStep )
    {
        return StepHome.findByPrimaryKey( nIdStep );
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Question object
     */
    private Question dataToObject( DAOUtil daoUtil )
    {
        Question question = new Question( );

        question.setId( daoUtil.getInt( "id_question" ) );
        question.setTitle( daoUtil.getString( "title" ) );
        question.setCode( daoUtil.getString( "code" ) );
        question.setDescription( daoUtil.getString( "description" ) );
        question.setIdEntry( daoUtil.getInt( "id_entry" ) );
        question.setEntry( getQuestionEntry( question.getIdEntry( ) ) );
        question.setIdStep( daoUtil.getInt( "id_step" ) );
        question.setVisibleMultiviewGlobal( daoUtil.getBoolean( "is_visible_multiview_global" ) );
        question.setVisibleMultiviewFormSelected( daoUtil.getBoolean( "is_visible_multiview_form_selected" ) );
        question.setColumnTitle( daoUtil.getString( "column_title" ) );
        question.setStep( getQuestionStep( question.getIdStep( ) ) );

        return question;
    }
}
